package com.task.server.service;

import com.task.server.config.ClientURL;
import com.task.server.dto.SecheduledRequestDTO;
import com.task.server.entity.SecheduledTaskInfo;
import com.task.server.entity.SecheduledTaskPiece;
import com.task.server.entity.TaskExecuteLog;
import com.task.server.repository.ISecheduledTaskPieceRepository;
import com.task.server.repository.ITaskExecuteLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
public class SecheduledRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SecheduledRunnable.class);

    private SecheduledTaskInfo taskInfo;
    private ClientServiceChoose clientChoose;
    private RestTemplate restTemplate;
    private ITaskExecuteLogRepository executeLogRepository;
    private Long exceCount;
    private ISecheduledTaskPieceRepository taskPieceRepository;

    public SecheduledRunnable() {
    }

    public void setExceCount(Long exceCount) {
        this.exceCount = exceCount;
    }

    public void setTaskInfo(SecheduledTaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }


    public void setExecuteLogRepository(ITaskExecuteLogRepository executeLogRepository) {
        this.executeLogRepository = executeLogRepository;
    }

    public void setTaskPieceRepository(ISecheduledTaskPieceRepository taskPieceRepository) {
        this.taskPieceRepository = taskPieceRepository;
    }

    public void setClientChoose(ClientServiceChoose clientChoose) {
        this.clientChoose = clientChoose;
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
        List<SecheduledTaskPiece> pieceList = taskPieceRepository.findByTaskIdAndStatus(taskInfo.getId(), SecheduledTaskPiece.NORMAL);
        if(CollectionUtils.isEmpty(pieceList)) {
            this.exeNoPiece(executeLog);
        } else {
            this.exePiece(executeLog, pieceList);
        }
    }


    /**
     * 不分片
     * @param executeLog
     */
    private void exeNoPiece(TaskExecuteLog executeLog) {
        ServiceInstance instance = null;
        try {
            instance = clientChoose.getClientServiceInstance(taskInfo.getServiceName());
        } catch (Exception e) {
            logger.error(e.getMessage() + "，logId=" + executeLog.getId(), e);
            executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
            executeLog.setResult(e.getMessage());
        }
        if(instance != null)  {
            sendRequest(executeLog, instance, "");
        }
        executeLogRepository.save(executeLog);
    }

    /**
     * 执行分片任务
     */
    private void exePiece(TaskExecuteLog executeLog, List<SecheduledTaskPiece> pieceList) {
        List<ServiceInstance> serviceList;
        try {
            serviceList = clientChoose.getClientServiceList(taskInfo.getServiceName());
        } catch (Exception e) {
            logger.error(e.getMessage() + " logId=" + executeLog.getId(), e);
            executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
            executeLog.setResult(e.getMessage());
            executeLogRepository.save(executeLog);
            return;
        }
        List<ServiceInstance> noChooseList = this.getNoChooseService(serviceList);
        for(SecheduledTaskPiece taskPiece : pieceList) {
            TaskExecuteLog pieceLog = new TaskExecuteLog();
            BeanUtils.copyProperties(executeLog, pieceLog);
            String logId = UUID.randomUUID().toString().replace("-", "");
            pieceLog.setId(logId);
            ServiceInstance instance = this.chooseService(noChooseList, serviceList);
            pieceLog.setPieceId(taskPiece.getId());
            sendRequest(pieceLog, instance, taskPiece.getParameters());
            executeLogRepository.save(pieceLog);
        }
    }

    private void sendRequest(TaskExecuteLog executeLog, ServiceInstance instance, String parameters) {
        executeLog.setTargetHostPort(instance.getHost() + ":" + instance.getPort());
        SecheduledRequestDTO requestDTO = new SecheduledRequestDTO();
        requestDTO.setClassName(taskInfo.getClassName());
        requestDTO.setLogId(executeLog.getId());
        requestDTO.setParameters(parameters);
        executeLog.setRequestBody(requestDTO.toString());
        URI uri = URI.create("http://" + instance.getHost() + ":" + instance.getPort() + ClientURL.EXECUTE_SECHEDULED_URL);
        executeLog.setSendRequestTime(new Date());
        try {
            restTemplate.postForObject(uri, requestDTO, String.class);
            executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_SUCCESS);
        } catch (Exception e) {
            logger.error("发送定时任务请求返回异常，logId=" + executeLog.getId(), e);
            executeLog.setStatus(TaskExecuteLog.SEND_REQUEST_FAILURE);
            executeLog.setResult(e.getMessage());
        } finally {
            executeLog.setReceiveResponseTime(new Date());
        }
    }

    /**
     * 浅层复制
     * @param serviceList
     * @return
     */
    private List<ServiceInstance> getNoChooseService(List<ServiceInstance> serviceList) {
        List<ServiceInstance> noChooseList = new ArrayList<>();
        for(ServiceInstance instance : serviceList) {
            noChooseList.add(instance);
        }
        return noChooseList;
    }

    /**
     * 选择客户端服务
     * @param serviceList
     * @return
     */
    private ServiceInstance chooseService(List<ServiceInstance> noChooseList, List<ServiceInstance> serviceList) {
        if(CollectionUtils.isEmpty(noChooseList)) {
            noChooseList = getNoChooseService(serviceList);
        }
        int index = new Random().nextInt(noChooseList.size());
        ServiceInstance instance = noChooseList.get(index);
        noChooseList.remove(index);
        return instance;
    }
}
