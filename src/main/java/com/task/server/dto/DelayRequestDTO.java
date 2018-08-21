package com.task.server.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Data
@ToString
public class DelayRequestDTO {

    //
    private String logId;
    //业务名称
    private String bizName;
    //业务参数
    private String bizParameters;

}
