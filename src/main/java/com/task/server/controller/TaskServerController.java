package com.task.server.controller;

import com.task.server.dto.SecheduledTaskPieceDTO;
import com.task.server.entity.DelayTaskInfo;
import com.task.server.entity.SecheduledTaskInfo;
import com.task.server.entity.SecheduledTaskPiece;
import com.task.server.entity.TaskExecuteLog;
import com.task.server.service.TaskServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class TaskServerController {

    @Autowired
    private TaskServerService serverService;

    @PostMapping("/task_server/add_secheduled_piece")
    public void addSecheduledPiece(@RequestBody SecheduledTaskPieceDTO pieceDTO) {
        serverService.addSecheduledPiece(pieceDTO);
    }

    @PostMapping("/task_server/del_secheduled_piece/{pieceId}")
    public void delSecheduledPiece(@PathVariable("pieceId")String pieceId) {
        serverService.delSecheduledPiece(pieceId);
    }

    @GetMapping("/task_server/get_secheduled_piece_list/{taskId}")
    public List<SecheduledTaskPiece> getSecheduledTaskPieceList(@PathVariable("taskId")String taskId) {
        return serverService.getSecheduledTaskPieceList(taskId);
    }

    @PostMapping("/task_server/update_secheduled_status/{taskId}/{status}")
    public void updateSecheduledStatus(@PathVariable("taskId")String taskId, @PathVariable("status")String status) {
        serverService.updateSecheduledStatus(taskId, status);
    }

    @PostMapping("/task_server/cancel_delay_task/{taskId}")
    public void cancelDelayTask(@PathVariable("taskId")String taskId) {
        serverService.cancelDelayTask(taskId);
    }

    @PostMapping("/task_server/update_delay_exectime/{taskId}")
    public void updateDelayExectime(@PathVariable("taskId")String taskId, @RequestBody Map<String, String> map) {
        serverService.updateDelayExectime(taskId, map.get("exectime").toString());
    }

    @GetMapping("/task_server/get_secheduled_task_pager")
    public Map secheduledTaskPager(@RequestParam(name = "page", required = true, defaultValue = "1")Integer page,
                                   @RequestParam(name = "limit", required = true, defaultValue = "20")Integer limit,
                                   @RequestParam(name = "serviceName", required = false)String serviceName,
                                   @RequestParam(name = "className", required = false)String className) {
        return serverService.secheduledTaskPager(page, limit, serviceName, className);
    }

    @GetMapping("/task_server/get_delay_task_pager")
    public Map delayTaskPager(@RequestParam(name = "page", required = true, defaultValue = "1")Integer page,
                                   @RequestParam(name = "limit", required = true, defaultValue = "20")Integer limit,
                                   @RequestParam(name = "archive", required = true, defaultValue = "0")Integer archive,
                                   @RequestParam(name = "taskId", required = false)String taskId,
                                   @RequestParam(name = "bizName", required = false)String bizName,
                                   @RequestParam(name = "bizParameters", required = false)String bizParameters) {
        return serverService.delayTaskPager(page, limit, archive, taskId, bizName, bizParameters);
    }

    @GetMapping("/task_server/get_task_log_pager")
    public Map taskLogPager(@RequestParam(name = "page", required = true, defaultValue = "1")Integer page,
                              @RequestParam(name = "limit", required = true, defaultValue = "20")Integer limit,
                              @RequestParam(name = "taskId", required = false)String taskId,
                              @RequestParam(name = "exceCount", required = false)Long exceCount,
                              @RequestParam(name = "startTime", required = false)String startTime,
                              @RequestParam(name = "endTime", required = false)String endTime) throws Exception {
        return serverService.taskLogPager(page, limit, taskId, exceCount, startTime, endTime);
    }

}
