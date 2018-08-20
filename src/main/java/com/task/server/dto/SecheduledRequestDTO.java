package com.task.server.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Data
@ToString
public class SecheduledRequestDTO {

    //类名
    private String className;
    //
    private String logId;
    //参数
    private String parameters;

}
