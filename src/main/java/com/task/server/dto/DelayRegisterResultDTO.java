package com.task.server.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Data
public class DelayRegisterResultDTO {

    public static final String SUCCESS = "success";
    public static final String PARAMETER_ERROR = "parameter_error";
    public static final String TASK_EXISTS = "task_exists";

    private String code;

    private String message;

    private List<String> taskIdList;

}
