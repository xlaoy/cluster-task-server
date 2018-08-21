package com.task.server.controller;

import com.task.server.dto.TaskResultDTO;
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

    @PostMapping("/task_server/task_feedback/{logId}")
    @ApiOperation(response = void.class, value = "定时任务执行结果反馈")
    public void taskFeedback(@PathVariable("logId")String logId, @RequestBody TaskResultDTO resultDTO) {
        feedbackService.taskFeedback(logId, resultDTO);
    }

}
