package com.task.server.service;

import com.mongodb.client.result.UpdateResult;
import com.task.server.entity.SecheduledTaskInfo;
import com.task.server.repository.ISecheduledExecuteLogRepository;
import com.task.server.repository.ISecheduledTaskInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
@Slf4j
@Component
public class TaskExecuteService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ThreadPoolTaskExecutor sendRequestkExecutor;
    @Autowired
    private ISecheduledExecuteLogRepository executeLogRepository;

    /**
     * 执行定时任务
     */
    public void executeSecheduledTask() {
        Date now = new Date();
        Query query = Query.query(Criteria.where("activation").is(SecheduledTaskInfo.ACTIVATE)
                            .and("status").is(SecheduledTaskInfo.ENABLE)
                            .and("nextExceTime").lte(now));
        List<SecheduledTaskInfo> taskList = mongoTemplate.find(query, SecheduledTaskInfo.class);
        if(CollectionUtils.isEmpty(taskList)) {
            return;
        }
        for(SecheduledTaskInfo taskInfo : taskList) {
            Query updateQuery = Query.query(Criteria.where("id").is(taskInfo.getId())
                    .and("exceCount").is(taskInfo.getExceCount()));
            Date nextTime = new CronSequenceGenerator(taskInfo.getCron()).next(taskInfo.getNextExceTime());
            Update update = Update.update("nextExceTime", nextTime).set("exceCount", taskInfo.getExceCount() + 1);
            UpdateResult result = mongoTemplate.updateFirst(updateQuery, update, SecheduledTaskInfo.class);
            if(result.getModifiedCount() == 1) {
                SecheduledRunnable runnable = new SecheduledRunnable(taskInfo.getId(), taskInfo.getServiceName());
                runnable.setLoadBalancerClient(loadBalancerClient);
                runnable.setRestTemplate(restTemplate);
                runnable.setBeginTime(new Date());
                runnable.setClassName(taskInfo.getClassName());
                runnable.setExecuteLogRepository(executeLogRepository);
                sendRequestkExecutor.execute(runnable);
            }
        }
    }

}
