package com.task.server.controller;

import com.task.server.dto.SecheduledRegisterDTO;
import com.task.server.service.TaskRegisterService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class TaskRegisterController {

    @Autowired
    private TaskRegisterService taskRegisterService;

    @PostMapping("/task_server/register_secheduled_task")
    @ApiOperation(response = void.class, value = "注册定时任务")
    public void registerSecheduledTask(@RequestBody SecheduledRegisterDTO registerDTO) {
        taskRegisterService.registerSecheduledTask(registerDTO);
    }

    @PostMapping("/task_server/register_delay_task")
    @ApiOperation(response = void.class, value = "注册延迟任务")
    public void registerDelayTask() {
    }
}
