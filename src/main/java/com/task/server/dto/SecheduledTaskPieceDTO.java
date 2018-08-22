package com.task.server.dto;

import lombok.Data;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Data
public class SecheduledTaskPieceDTO {
    //任务id
    private String taskId;
    //自定义参数
    private String parameters;
}
