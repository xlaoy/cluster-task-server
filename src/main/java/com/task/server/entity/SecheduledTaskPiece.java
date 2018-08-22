package com.task.server.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.task.server.config.BeanConfig;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Data
@Document(collection = "secheduled_task_piece")
public class SecheduledTaskPiece {

    public static final String NORMAL = "normal";
    public static final String DELETE = "delete";

    @Id
    private String id;
    //创建时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date createTime;
    //任务id
    private String taskId;
    //自定义参数
    private String parameters;
    //自定义参数
    private String status;
}
