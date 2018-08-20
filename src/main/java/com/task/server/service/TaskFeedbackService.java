package com.task.server.service;

import com.task.server.dto.SecheduledResponseDTO;
import com.task.server.entity.SecheduledExecuteLog;
import com.task.server.repository.ISecheduledExecuteLogRepository;
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
    private ISecheduledExecuteLogRepository secheduledExecuteLogRepository;


    public void secheduledFeedback(String logId, SecheduledResponseDTO responseDTO) {
        Optional<SecheduledExecuteLog> optional = secheduledExecuteLogRepository.findById(logId);
        if(optional.isPresent()) {
            SecheduledExecuteLog executeLog = optional.get();
            executeLog.setStatus(responseDTO.getStatus());
            executeLog.setResult(responseDTO.getResult());
            executeLog.setFeedbackTime(new Date());
            secheduledExecuteLogRepository.save(executeLog);
        }
    }

}
