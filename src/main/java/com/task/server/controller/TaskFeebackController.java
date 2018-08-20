package com.task.server.controller;

import com.task.server.dto.SecheduledResponseDTO;
import com.task.server.service.TaskFeedbackService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class TaskFeebackController {

    @Autowired
    private TaskFeedbackService feedbackService;

    @PostMapping("/task_server/secheduled_feedback/{logId}")
    @ApiOperation(response = void.class, value = "定时任务执行结果反馈")
    public void registerSecheduledTask(@PathVariable("logId")String logId, @RequestBody SecheduledResponseDTO responseDTO) {
        feedbackService.secheduledFeedback(logId, responseDTO);
    }

    @PostMapping("/task_server/delay_feedback/{logId}")
    @ApiOperation(response = void.class, value = "延迟任务执行结果反馈")
    public void registerDelayTask() {
    }
}
