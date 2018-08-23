package com.task.server.service;

import com.task.server.config.BeanConfig;
import com.task.server.dto.SecheduledTaskPieceDTO;
import com.task.server.entity.*;
import com.task.server.repository.IDelayTaskInfoRepository;
import com.task.server.repository.ISecheduledTaskInfoRepository;
import com.task.server.repository.ISecheduledTaskPieceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018/8/22 0022.
 */
@Component
public class TaskServerService {

    @Autowired
    private ISecheduledTaskPieceRepository taskPieceRepository;
    @Autowired
    private ISecheduledTaskInfoRepository secheduledTaskInfoRepository;
    @Autowired
    private IDelayTaskInfoRepository delayTaskInfoRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    public void addSecheduledPiece(SecheduledTaskPieceDTO pieceDTO) {
        Assert.notNull(pieceDTO.getTaskId(), "taskId不能为空");
        Assert.notNull(pieceDTO.getParameters(), "parameters不能为空");
        Optional<SecheduledTaskInfo> optional = secheduledTaskInfoRepository.findById(pieceDTO.getTaskId());
        if(!optional.isPresent()) {
            throw new RuntimeException("任务不存在");
        }
        SecheduledTaskInfo taskInfo = optional.get();
        if(!SecheduledTaskInfo.ACTIVATE.equals(taskInfo.getActivation())) {
            throw new RuntimeException("任务不是激活状态");
        }
        String parameters = pieceDTO.getParameters();
        parameters = parameters.trim();
        if(!CollectionUtils.isEmpty(taskPieceRepository.findByTaskIdAndStatusAndParameters(pieceDTO.getTaskId(), SecheduledTaskPiece.NORMAL, parameters))) {
            throw new RuntimeException("分片已存在");
        }
        SecheduledTaskPiece taskPiece = new SecheduledTaskPiece();
        taskPiece.setStatus(SecheduledTaskPiece.NORMAL);
        taskPiece.setParameters(parameters);
        taskPiece.setTaskId(pieceDTO.getTaskId());
        taskPieceRepository.save(taskPiece);
    }

    public void delSecheduledPiece(String pieceId) {
        Optional<SecheduledTaskPiece> optional = taskPieceRepository.findById(pieceId);
        if(!optional.isPresent()) {
            throw new RuntimeException("分片不存在");
        }
        SecheduledTaskPiece taskPiece = optional.get();
        taskPiece.setStatus(SecheduledTaskPiece.DELETE);
        taskPieceRepository.save(taskPiece);
    }

    public void cancelDelayTask(String taskId) {
        Optional<DelayTaskInfo> optional = delayTaskInfoRepository.findById(taskId);
        if(!optional.isPresent()) {
            throw new RuntimeException("任务不存在");
        }
        DelayTaskInfo taskInfo = optional.get();
        if(!DelayTaskInfo.WAITING.equals(taskInfo.getStatus())) {
            throw new RuntimeException("任务不是待执行状态");
        }
        taskInfo.setStatus(DelayTaskInfo.CANCEL);
        taskInfo.setRemark("管理任务手动取消");
        delayTaskInfoRepository.save(taskInfo);
    }

    public void updateDelayExectime(String taskId, String exectime) throws Exception {
        Optional<DelayTaskInfo> optional = delayTaskInfoRepository.findById(taskId);
        if(!optional.isPresent()) {
            throw new RuntimeException("任务不存在");
        }
        DelayTaskInfo taskInfo = optional.get();
        if(!DelayTaskInfo.WAITING.equals(taskInfo.getStatus())) {
            throw new RuntimeException("任务不是待执行状态");
        }
        taskInfo.setExecuteTime(new SimpleDateFormat(BeanConfig.YYYY_MM_DD_HH_MM_SS).parse(exectime));
        delayTaskInfoRepository.save(taskInfo);
    }

    public Map getSecheduledTaskPieceList(String taskId) {
        if(StringUtils.isEmpty(taskId)) {
            return pageMap(1, new ArrayList());
        }
        return pageMap(1, taskPieceRepository.findByTaskIdAndStatus(taskId, SecheduledTaskPiece.NORMAL));
    }

    public void updateSecheduledStatus(String taskId, String status) {
        Optional<SecheduledTaskInfo> optional = secheduledTaskInfoRepository.findById(taskId);
        if(!optional.isPresent()) {
            throw new RuntimeException("任务不存在");
        }
        SecheduledTaskInfo taskInfo = optional.get();
        if(!SecheduledTaskInfo.ACTIVATE.equals(taskInfo.getActivation())) {
            throw new RuntimeException("任务不是激活状态");
        }
        taskInfo.setStatus(status);
        secheduledTaskInfoRepository.save(taskInfo);
    }

    public Map secheduledTaskPager(Integer page, Integer limit, String serviceName, String className) {
        Criteria criteria = new Criteria();
        if(!StringUtils.isEmpty(serviceName)) {
            criteria.and("serviceName").is(serviceName);
        }
        if(!StringUtils.isEmpty(className)) {
            criteria.and("className").regex(className);
        }
        if(page < 1) {
            page = 1;
        }
        Pageable pageRequest = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
        Query query = Query.query(criteria).with(pageRequest);
        long count = mongoTemplate.count(query, SecheduledTaskInfo.class);
        List<SecheduledTaskInfo> list = new ArrayList<>();
        if(count > 0) {
            list = mongoTemplate.find(query, SecheduledTaskInfo.class);
        }
        return pageMap(count, list);
    }

    public Map delayTaskPager(Integer page, Integer limit, Integer archive, String taskId,
                                              String bizName, String bizParameters) {
        if(page < 1) {
            page = 1;
        }
        if(archive == 0) {
            Criteria criteria = new Criteria();
            if(!StringUtils.isEmpty(taskId)) {
                criteria.and("id").is(taskId);
            }
            if(!StringUtils.isEmpty(bizName)) {
                criteria.and("bizName").is(bizName);
            }
            if(!StringUtils.isEmpty(bizParameters)) {
                criteria.and("bizParameters").regex(bizParameters);
            }
            Pageable pageRequest = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
            Query query = Query.query(criteria).with(pageRequest);
            long count = mongoTemplate.count(query, DelayTaskInfo.class);
            List<DelayTaskInfo> list = new ArrayList<>();
            if(count > 0) {
                list = mongoTemplate.find(query, DelayTaskInfo.class);
            }
            return pageMap(count, list);
        } else {
            List<DelayTaskInfo> contentList = new ArrayList<>();
            Map map = delayHistoryTaskPager(page, limit, taskId, bizName, bizParameters);
            List<DelayTaskInfoHistory> list = (List<DelayTaskInfoHistory>)map.get("data");
            list.forEach(his -> {
                DelayTaskInfo info = new DelayTaskInfo();
                BeanUtils.copyProperties(his, info);
                info.setId(his.getTaskId());
                contentList.add(info);
            });
            map.put("data", contentList);
            return map;
        }
    }

    private Map delayHistoryTaskPager(Integer page, Integer limit, String taskId, String bizName, String bizParameters) {
        Criteria criteria = new Criteria();
        if(!StringUtils.isEmpty(taskId)) {
            criteria.and("taskId").is(taskId);
        }
        if(!StringUtils.isEmpty(bizName)) {
            criteria.and("bizName").is(bizName);
        }
        if(!StringUtils.isEmpty(bizParameters)) {
            criteria.and("bizParameters").regex(bizParameters);
        }
        Pageable pageRequest = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
        Query query = Query.query(criteria).with(pageRequest);
        long count = mongoTemplate.count(query, DelayTaskInfoHistory.class);
        List<DelayTaskInfoHistory> list = new ArrayList<>();
        if(count > 0) {
            list = mongoTemplate.find(query, DelayTaskInfoHistory.class);
        }
        return pageMap(count, list);
    }


    public Map taskLogPager(Integer page, Integer limit, String taskId, Long exceCount,
                            String startTime, String endTime) throws Exception {
        Criteria criteria = new Criteria();
        if(!StringUtils.isEmpty(taskId)) {
            criteria.and("taskId").is(taskId);
        }
        if(exceCount != null) {
            criteria.and("exceCount").is(exceCount);
        }
        if(!StringUtils.isEmpty(startTime)) {
            Date date = new SimpleDateFormat(BeanConfig.YYYY_MM_DD_HH_MM_SS).parse(startTime);
            criteria.and("beginTime").gte(date);
        }
        if(!StringUtils.isEmpty(endTime)) {
            Date date = new SimpleDateFormat(BeanConfig.YYYY_MM_DD_HH_MM_SS).parse(endTime);
            criteria.and("beginTime").lte(date);
        }
        if(page < 1) {
            page = 1;
        }
        Pageable pageRequest = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "beginTime"));
        Query query = Query.query(criteria).with(pageRequest);
        long count = mongoTemplate.count(query, TaskExecuteLog.class);
        List<TaskExecuteLog> list = mongoTemplate.find(query, TaskExecuteLog.class);
        return pageMap(count, list);
    }

    private Map pageMap(long count, List list) {
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);
        map.put("data", list);
        map.put("code", "0");
        map.put("msg", "");
        return map;
    }
}
