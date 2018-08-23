package com.task.server.service;

import com.task.server.config.ClientURL;
import com.task.server.dto.DelayRequestDTO;
import com.task.server.entity.DelayTaskInfo;
import com.task.server.entity.TaskExecuteLog;
import com.task.server.repository.ITaskExecuteLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
public class DelayRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(DelayRunnable.class);

    private DelayTaskInfo taskInfo;
    private ClientServiceChoose clientChoose;
    private RestTemplate restTemplate;
    private ITaskExecuteLogRepository executeLogRepository;
    private Long exceCount;
    private Integer retryCount;
    private MongoTemplate mongoTemplate;

    public DelayRunnable() {
    }

    public void setExceCount(Long exceCount) {
        this.exceCount = exceCount;
    }

    public void setTaskInfo(DelayTaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    public void setClientChoose(ClientServiceChoose clientChoose) {
        this.clientChoose = clientChoose;
    }

    public void setExecuteLogRepository(ITaskExecuteLogRepository executeLogRepository) {
        this.executeLogRepository = executeLogRepository;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
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
        executeLog.setTaskType(TaskExecuteLog.DELAY);
        ServiceInstance instance = null;
        try {
            instance = this.clientChoose.getClientServiceInstance(taskInfo.getExecuteServiceName());
        } catch (Exception e) {
            logger.error(e.getMessage() + " ，logId=" + logId, e);
            executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
            executeLog.setResult(e.getMessage());
        }
        if(instance != null)  {
            executeLog.setTargetHostPort(instance.getHost() + ":" + instance.getPort());
            DelayRequestDTO requestDTO = new DelayRequestDTO();
            requestDTO.setLogId(logId);
            requestDTO.setBizName(taskInfo.getBizName());
            requestDTO.setBizParameters(taskInfo.getBizParameters());
            executeLog.setRequestBody(requestDTO.toString());
            URI uri = URI.create("http://" + instance.getHost() + ":" + instance.getPort() + ClientURL.EXECUTE_DELAY_URL);
            executeLog.setSendRequestTime(new Date());
            try {
                restTemplate.postForObject(uri, requestDTO, String.class);
                executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_SUCCESS);
            } catch (Exception e) {
                logger.error("发送延迟任务请求返回异常，logId=" + logId, e);
                executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
                executeLog.setResult(e.getMessage());
            } finally {
                executeLog.setReceiveResponseTime(new Date());
            }
        }
        executeLogRepository.save(executeLog);
        if(TaskExecuteLog.SEND_REQUEST_FAILURE.equals(executeLog.getStatus())) {
            if(exceCount.intValue() >= retryCount.intValue()) { //重试
                Query updateQuery = Query.query(Criteria.where("id").is(taskInfo.getId()));
                Update update = Update.update("status", DelayTaskInfo.CANCEL).set("remark", "系统重试" + retryCount + "次都失败，自动取消任务");
                mongoTemplate.updateFirst(updateQuery, update, DelayTaskInfo.class);
                logger.error("延迟任务系统重试" + retryCount + "次都失败，自动取消任务，taskId=" + taskInfo.getId());
            } else {
                Query updateQuery = Query.query(Criteria.where("id").is(taskInfo.getId()));
                Update update = Update.update("status", DelayTaskInfo.WAITING);
                mongoTemplate.updateFirst(updateQuery, update, DelayTaskInfo.class);

            }
        }
    }
}
