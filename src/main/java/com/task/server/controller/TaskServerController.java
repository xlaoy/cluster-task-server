package com.task.server.controller;

import com.task.server.entity.SecheduledTaskInfo;
import com.task.server.repository.ISecheduledTaskInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class TaskServerController {

    @Autowired
    private ISecheduledTaskInfoRepository secheduledTaskInfoRepository;

    @GetMapping("/task_server/get_secheduled_task")
    public List<SecheduledTaskInfo> registerSecheduledTask() {
        return secheduledTaskInfoRepository.findAll();
    }

}
