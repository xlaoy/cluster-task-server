package com.task.server.service;

import com.task.server.dto.SecheduledRequestDTO;
import com.task.server.entity.SecheduledExecuteLog;
import com.task.server.repository.ISecheduledExecuteLogRepository;
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

    private String taskId;
    private String className;
    private String serviceName;
    private LoadBalancerClient loadBalancerClient;
    private RestTemplate restTemplate;
    private ISecheduledExecuteLogRepository executeLogRepository;
    private Date beginTime;
    private Long exceCount;

    private static final String TASK_CLIENT_EXECUTE_URL = "/task_client/execute_secheduled_task";

    public SecheduledRunnable() {
    }

    public void setExceCount(Long exceCount) {
        this.exceCount = exceCount;
    }

    public SecheduledRunnable(String taskId, String serviceName) {
        this.taskId = taskId;
        this.serviceName = serviceName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public void setExecuteLogRepository(ISecheduledExecuteLogRepository executeLogRepository) {
        this.executeLogRepository = executeLogRepository;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        SecheduledExecuteLog executeLog = new SecheduledExecuteLog();
        String logId = UUID.randomUUID().toString().replace("-", "");
        executeLog.setId(logId);
        executeLog.setTaskId(taskId);
        executeLog.setBeginTime(beginTime);
        executeLog.setExceCount(exceCount);
        ServiceInstance instance = this.loadBalancerClient.choose(serviceName.toUpperCase());
        if(instance == null) {
            executeLog.setStatus(SecheduledExecuteLog.SEND_REQUEST_FAILURE);
            executeLog.setResult(serviceName + " 没有发现可用服务实例");
        } else {
            executeLog.setTargetHostPort(instance.getHost() + ":" + instance.getPort());
            SecheduledRequestDTO sendDTO = new SecheduledRequestDTO();
            sendDTO.setClassName(className);
            sendDTO.setLogId(logId);
            sendDTO.setParameters("{}");
            executeLog.setRequestBody(sendDTO.toString());
            URI sendUri = URI.create(String.format("http://%s:%s" + TASK_CLIENT_EXECUTE_URL, instance.getHost(), instance.getPort()));
            executeLog.setSendRequestTime(new Date());
            try {
                restTemplate.postForObject(sendUri, sendDTO, String.class);
                executeLog.setStatus(SecheduledExecuteLog.SEND_REQUEST_SUCCESS);
            } catch (Exception e) {
                logger.error("发送定时任务请求返回异常，logId=" + logId, e);
                executeLog.setStatus(SecheduledExecuteLog.SEND_REQUEST_FAILURE);
                executeLog.setResult(e.getMessage());
            } finally {
                executeLog.setReceiveResponseTime(new Date());
            }
        }
        executeLogRepository.save(executeLog);
    }
}
