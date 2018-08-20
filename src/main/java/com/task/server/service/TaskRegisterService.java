package com.task.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.server.entity.SecheduledRegisterLog;
import com.task.server.exception.JSONException;
import com.task.server.repository.ISecheduledRegisterLogRepository;
import com.task.server.repository.ISecheduledTaskInfoRepository;
import com.task.server.dto.SecheduledRegisterDTO;
import com.task.server.entity.SecheduledTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Slf4j
@Component
public class TaskRegisterService {

    @Autowired
    private ISecheduledTaskInfoRepository secheduledTaskInfoRepository;
    @Autowired
    private ISecheduledRegisterLogRepository secheduledRegisterLogRepository;
    @Autowired
    private ObjectMapper mapper;

    /**
     *
     * @param registerDTO
     */
    public void registerSecheduledTask(SecheduledRegisterDTO registerDTO) {
        Date now = new Date();
        SecheduledRegisterLog registerLog = new SecheduledRegisterLog();
        registerLog.setServiceName(registerDTO.getServiceName());
        registerLog.setHostAndPort(registerDTO.getHostAndPort());
        try {
            registerLog.setSecheduledInfoList(mapper.writeValueAsString(registerDTO.getSecheduledInfoList()));
        } catch (IOException e) {
            log.error("josn解析错误", e);
            throw new JSONException("josn解析错误");
        }
        registerLog.setStatus(SecheduledRegisterLog.RECEIVE_REQUEST);
        registerLog.setCreateTime(now);
        secheduledRegisterLogRepository.save(registerLog);
        try {
            List<SecheduledTaskInfo> oldTaskList = secheduledTaskInfoRepository.findByServiceName(registerDTO.getServiceName());
            //
            this.addSecheduledTask(now, oldTaskList, registerDTO);
            //
            this.updateSecheduledTask(now,  oldTaskList, registerDTO.getSecheduledInfoList());

            registerLog.setStatus(SecheduledRegisterLog.REGISTER_SUCCESS);
            secheduledRegisterLogRepository.save(registerLog);
        } catch (Exception e) {
            log.error("定时任务注册异常，registerId={}", registerLog.getId(), e);
            registerLog.setStatus(SecheduledRegisterLog.REGISTER_FAILURE);
            secheduledRegisterLogRepository.save(registerLog);
            throw e;
        }
    }

    private void addSecheduledTask(Date now, List<SecheduledTaskInfo> oldTaskList, SecheduledRegisterDTO registerDTO) {
        List<SecheduledRegisterDTO.SecheduledInfo> newTaskList = registerDTO.getSecheduledInfoList();
        for(SecheduledRegisterDTO.SecheduledInfo newTask : newTaskList) {
            boolean add = false;
            if(CollectionUtils.isEmpty(oldTaskList)) {
                add = true;
            } else {
                Optional<SecheduledTaskInfo> optional = oldTaskList.stream().filter(oldTask ->
                        oldTask.getClassName().equals(newTask.getClassName())
                ).findFirst();
                if(!optional.isPresent()) {
                    add = true;
                }
            }
            if(add) {
                SecheduledTaskInfo taskInfo = new SecheduledTaskInfo();
                taskInfo.setServiceName(registerDTO.getServiceName());
                taskInfo.setClassName(newTask.getClassName());
                taskInfo.setCron(newTask.getCron());
                taskInfo.setCreateTime(now);
                taskInfo.setUpdateTime(now);
                taskInfo.setStatus(SecheduledTaskInfo.ENABLE);
                taskInfo.setActivation(SecheduledTaskInfo.ACTIVATE);
                CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(newTask.getCron());
                Date exceTime = cronSequenceGenerator.next(now);
                taskInfo.setFristExceTime(exceTime);
                taskInfo.setNextExceTime(exceTime);
                taskInfo.setExceCount(0l);
                secheduledTaskInfoRepository.save(taskInfo);
            }
        }
    }

    private void updateSecheduledTask(Date now, List<SecheduledTaskInfo> oldTaskList, List<SecheduledRegisterDTO.SecheduledInfo> newTaskList) {
        if(CollectionUtils.isEmpty(oldTaskList)) {
            return;
        }
        for(SecheduledTaskInfo oldTask : oldTaskList) {
            Optional<SecheduledRegisterDTO.SecheduledInfo> optional = newTaskList.stream().filter(newTask ->
                    oldTask.getClassName().equals(newTask.getClassName())
            ).findFirst();
            if(optional.isPresent()) {
                boolean update = false;
                if(!optional.get().getCron().equals(oldTask.getCron())) {
                    oldTask.setCron(optional.get().getCron());
                    update = true;
                }
                if(SecheduledTaskInfo.INVALID.equals(oldTask.getActivation())) {
                    oldTask.setActivation(SecheduledTaskInfo.ACTIVATE);
                    update = true;
                }
                if(update) {
                    oldTask.setUpdateTime(now);
                    secheduledTaskInfoRepository.save(oldTask);
                }
            } else {
                oldTask.setStatus(SecheduledTaskInfo.DISABLE);
                oldTask.setActivation(SecheduledTaskInfo.INVALID);
                oldTask.setUpdateTime(now);
                secheduledTaskInfoRepository.save(oldTask);
            }
        }
    }

}
