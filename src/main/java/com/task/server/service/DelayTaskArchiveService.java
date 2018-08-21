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

import java.util.List;

/**
 * Created by Administrator on 2018/8/21 0021.
 */
@Component
public class DelayTaskArchiveService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IDelayTaskInfoHistoryRepository taskInfoHistoryRepository;

    /**
     *
     */
    public void archive() {
        logger.info("延迟任务开始归档");
        Query query = Query.query(Criteria.where("status").in(DelayTaskInfo.CANCEL, DelayTaskInfo.FINISH).size(1000));
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
        }
    }
}
