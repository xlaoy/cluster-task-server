package com.task.server.task;

import com.task.server.config.BeanConfig;
import com.task.server.service.SecheduledRunnable;
import com.task.server.service.TaskExecuteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
@Component
public class TaskJob {

    @Autowired
    private TaskExecuteService executeService;

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
    @Scheduled(fixedDelay = 100)
    public void executeDelayTask() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }
    }

}
