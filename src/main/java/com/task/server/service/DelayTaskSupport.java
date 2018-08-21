package com.task.server.service;

import com.task.server.entity.DelayTaskInfo;
import com.task.server.entity.DelayTaskInfoHistory;
import com.task.server.entity.SecheduledTaskInfo;
import com.task.server.repository.IDelayTaskInfoHistoryRepository;
import com.task.server.repository.IDelayTaskInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/21 0021.
 */
@Component
public class DelayTaskSupport {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IDelayTaskInfoRepository taskInfoRepository;
    @Autowired
    private IDelayTaskInfoHistoryRepository taskInfoHistoryRepository;

    /**
     *
     */
    public void archive() {
        logger.info("延迟任务开始归档");
        Query query = Query.query(Criteria.where("status").in(
                DelayTaskInfo.FINISH,
                DelayTaskInfo.CANCEL,
                DelayTaskInfo.FEEDBACK_TIMEOUT
        )).limit(1000);
        List<DelayTaskInfo> taskInfoList = mongoTemplate.find(query, DelayTaskInfo.class);
        logger.info("归档条数，size=" + taskInfoList.size());
        if(CollectionUtils.isEmpty(taskInfoList)) {
            return;
        }
        for(DelayTaskInfo taskInfo : taskInfoList) {
            DelayTaskInfoHistory history = new DelayTaskInfoHistory();
            BeanUtils.copyProperties(taskInfo, history);
            history.setId(null);
            history.setTaskId(taskInfo.getId());
            taskInfoHistoryRepository.save(history);
            taskInfoRepository.deleteById(taskInfo.getId());
        }
    }

    /**
     * executeing 状态
     * executeTime 已经超过一天了
     */
    public void feedbackTimeout() {
        logger.info("开始查询反馈超时的延迟任务");
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date before = calendar.getTime();
        Query query = Query.query(Criteria.where("status").is(DelayTaskInfo.EXECUTEING)
                                    .and("executeTime").lte(before));
        List<DelayTaskInfo> taskInfoList = mongoTemplate.find(query, DelayTaskInfo.class);
        logger.info("反馈超时的延迟任务条数，size=" + taskInfoList.size());
        if(CollectionUtils.isEmpty(taskInfoList)) {
            return;
        }
        for(DelayTaskInfo taskInfo : taskInfoList) {
            taskInfo.setStatus(DelayTaskInfo.FEEDBACK_TIMEOUT);
            taskInfoRepository.save(taskInfo);
        }
    }
}
