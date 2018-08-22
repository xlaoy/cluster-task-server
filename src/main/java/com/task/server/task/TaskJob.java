package com.task.server.task;

import com.task.server.service.DelayTaskSupport;
import com.task.server.service.TaskExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
//@Component
public class TaskJob {

    @Autowired
    private TaskExecuteService executeService;
    @Autowired
    private DelayTaskSupport delayTaskSupport;

    /**
     * 执行定时任务
     */
    @Scheduled(fixedDelay = 100)
    public void executeSecheduledTask() {
        executeService.executeSecheduledTask();
    }

    /**
     * 执行延迟任务
     */
    @Scheduled(fixedDelay = 1000)
    public void executeDelayTask() {
        executeService.executeDelayTask();
    }

    /**
     * 延迟任务归档
     * 每半小时跑一次
     */
    @Scheduled(cron = "0 0/30 * * * ? ")
    public void delayTaskArchive() {
        delayTaskSupport.archive();
    }

    /**
     * 延迟任务反馈超时
     * 每15分钟跑一次
     */
    @Scheduled(cron = "0 0/15 * * * ? ")
    public void delayTaskFeedbackTimeout() {
        delayTaskSupport.feedbackTimeout();
    }

}
