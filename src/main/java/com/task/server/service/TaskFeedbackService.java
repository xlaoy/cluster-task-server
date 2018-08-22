package com.task.server.service;

import com.task.server.dto.TaskResultDTO;
import com.task.server.entity.DelayTaskInfo;
import com.task.server.entity.TaskExecuteLog;
import com.task.server.repository.IDelayTaskInfoRepository;
import com.task.server.repository.ITaskExecuteLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
@Component
public class TaskFeedbackService {

    @Autowired
    private ITaskExecuteLogRepository executeLogRepository;
    @Autowired
    private IDelayTaskInfoRepository delayTaskInfoRepository;


    public void taskFeedback(String logId, TaskResultDTO resultDTO) {
        Optional<TaskExecuteLog> optional = executeLogRepository.findById(logId);
        if(optional.isPresent()) {
            TaskExecuteLog executeLog = optional.get();
            executeLog.setStatus(resultDTO.getStatus());
            executeLog.setResult(resultDTO.getResult());
            executeLog.setFeedbackTime(new Date());
            executeLogRepository.save(executeLog);
            if(TaskExecuteLog.DELAY.equals(executeLog.getTaskType())) {
                Optional<DelayTaskInfo> optional1 = delayTaskInfoRepository.findById(executeLog.getTaskId());
                if(optional1.isPresent()) {
                    DelayTaskInfo taskInfo = optional1.get();
                    taskInfo.setStatus(DelayTaskInfo.FINISH);
                    delayTaskInfoRepository.save(taskInfo);
                }
            }
        }
    }

}
