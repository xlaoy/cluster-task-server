package com.task.server.service;

import com.task.server.config.ClientURL;
import com.task.server.dto.SecheduledRequestDTO;
import com.task.server.entity.SecheduledTaskInfo;
import com.task.server.entity.TaskExecuteLog;
import com.task.server.repository.ITaskExecuteLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
public class SecheduledRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SecheduledRunnable.class);

    private SecheduledTaskInfo taskInfo;
    private LoadBalancerClient loadBalancerClient;
    private RestTemplate restTemplate;
    private ITaskExecuteLogRepository executeLogRepository;
    private Long exceCount;

    public SecheduledRunnable() {
    }

    public void setExceCount(Long exceCount) {
        this.exceCount = exceCount;
    }

    public void setTaskInfo(SecheduledTaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public void setExecuteLogRepository(ITaskExecuteLogRepository executeLogRepository) {
        this.executeLogRepository = executeLogRepository;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        TaskExecuteLog executeLog = new TaskExecuteLog();
        String logId = UUID.randomUUID().toString().replace("-", "");
        executeLog.setId(logId);
        executeLog.setTaskId(taskInfo.getId());
        executeLog.setBeginTime(new Date());
        executeLog.setExceCount(exceCount);
        executeLog.setTaskType(TaskExecuteLog.SECHEDULED);
        ServiceInstance instance = null;
        try {
            instance = this.loadBalancerClient.choose(taskInfo.getServiceName().toUpperCase());
        } catch (Exception e) {
            logger.error(taskInfo.getServiceName() + " 选择服务实例异常，logId=" + logId, e);
            executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
            executeLog.setResult(taskInfo.getServiceName() + " 选择服务实例异常");
        }
        if(instance == null) {
            executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
            executeLog.setResult(taskInfo.getServiceName() + " 没有发现可用服务实例");
        } else {
            executeLog.setTargetHostPort(instance.getHost() + ":" + instance.getPort());
            SecheduledRequestDTO requestDTO = new SecheduledRequestDTO();
            requestDTO.setClassName(taskInfo.getClassName());
            requestDTO.setLogId(logId);
            requestDTO.setParameters("{}");
            executeLog.setRequestBody(requestDTO.toString());
            URI uri = URI.create("http://" + instance.getHost() + ":" + instance.getPort() + ClientURL.EXECUTE_SECHEDULED_URL);
            executeLog.setSendRequestTime(new Date());
            try {
                restTemplate.postForObject(uri, requestDTO, String.class);
                executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_SUCCESS);
            } catch (Exception e) {
                logger.error("发送定时任务请求返回异常，logId=" + logId, e);
                executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
                executeLog.setResult(e.getMessage());
            } finally {
                executeLog.setReceiveResponseTime(new Date());
            }
        }
        executeLogRepository.save(executeLog);
    }
}
