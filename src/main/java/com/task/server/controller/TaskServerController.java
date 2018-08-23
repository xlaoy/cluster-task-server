package com.task.server.controller;

import com.task.server.dto.SecheduledTaskPieceDTO;
import com.task.server.entity.SecheduledTaskPiece;
import com.task.server.service.TaskServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class TaskServerController {

    @Autowired
    private TaskServerService serverService;

    /**
     * 添加任务分片
     * @param pieceDTO
     */
    @PostMapping("/task_server/add_secheduled_piece")
    public void addSecheduledPiece(@RequestBody SecheduledTaskPieceDTO pieceDTO) {
        serverService.addSecheduledPiece(pieceDTO);
    }

    /**
     * 删除任务分片
     * @param pieceId
     */
    @PostMapping("/task_server/del_secheduled_piece/{pieceId}")
    public void delSecheduledPiece(@PathVariable("pieceId")String pieceId) {
        serverService.delSecheduledPiece(pieceId);
    }

    /**
     * 获取任务分片列表
     * @param page
     * @param limit
     * @param taskId
     * @return
     */
    @GetMapping("/task_server/get_secheduled_piece_list")
    public Map getSecheduledTaskPieceList(@RequestParam(name = "page", required = false, defaultValue = "1")Integer page,
                                          @RequestParam(name = "limit", required = false, defaultValue = "20")Integer limit,
                                          @RequestParam(name = "taskId", required = false)String taskId) {
        return serverService.getSecheduledTaskPieceList(taskId);
    }

    /**
     * 修改定时任务状态
     * @param taskId
     * @param status
     */
    @PostMapping("/task_server/update_secheduled_status/{taskId}/{status}")
    public void updateSecheduledStatus(@PathVariable("taskId")String taskId, @PathVariable("status")String status) {
        serverService.updateSecheduledStatus(taskId, status);
    }

    /**
     * 手动取消延时任务
     * @param taskId
     */
    @PostMapping("/task_server/cancel_delay_task_by_hand/{taskId}")
    public void cancelDelayTask(@PathVariable("taskId")String taskId) {
        serverService.cancelDelayTask(taskId);
    }

    /**
     * 修改延时任务执行时间
     * @param taskId
     * @param map
     * @throws Exception
     */
    @PostMapping("/task_server/update_delay_exectime/{taskId}")
    public void updateDelayExectime(@PathVariable("taskId")String taskId, @RequestBody Map<String, String> map) throws Exception {
        serverService.updateDelayExectime(taskId, map.get("exectime").toString());
    }

    /**
     * 定时任务分页
     * @param page
     * @param limit
     * @param serviceName
     * @param className
     * @return
     */
    @GetMapping("/task_server/get_secheduled_task_pager")
    public Map secheduledTaskPager(@RequestParam(name = "page", required = true, defaultValue = "1")Integer page,
                                   @RequestParam(name = "limit", required = true, defaultValue = "20")Integer limit,
                                   @RequestParam(name = "serviceName", required = false)String serviceName,
                                   @RequestParam(name = "className", required = false)String className) {
        return serverService.secheduledTaskPager(page, limit, serviceName, className);
    }

    /**
     * 延时任务分页
     * @param page
     * @param limit
     * @param archive
     * @param taskId
     * @param bizName
     * @param bizParameters
     * @return
     */
    @GetMapping("/task_server/get_delay_task_pager")
    public Map delayTaskPager(@RequestParam(name = "page", required = true, defaultValue = "1")Integer page,
                                   @RequestParam(name = "limit", required = true, defaultValue = "20")Integer limit,
                                   @RequestParam(name = "archive", required = true, defaultValue = "0")Integer archive,
                                   @RequestParam(name = "taskId", required = false)String taskId,
                                   @RequestParam(name = "bizName", required = false)String bizName,
                                   @RequestParam(name = "bizParameters", required = false)String bizParameters) {
        return serverService.delayTaskPager(page, limit, archive, taskId, bizName, bizParameters);
    }

    /**
     * 任务日志分页
     * @param page
     * @param limit
     * @param taskId
     * @param exceCount
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
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
