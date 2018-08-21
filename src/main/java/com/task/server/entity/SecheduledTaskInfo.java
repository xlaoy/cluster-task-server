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
@Document(collection = "secheduled_task_info")
public class SecheduledTaskInfo {

    public static final String ENABLE = "enable";
    public static final String DISABLE = "disable";

    public static final String ACTIVATE = "activate";
    public static final String INVALID = "invalid";

    @Id
    private String id;
    //服务名称
    private String serviceName;
    //类名称
    private String className;
    //执行时间
    private String cron;
    //任务状态
    private String status;
    //创建时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date createTime;
    //修改时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date updateTime;
    //激活状态
    private String activation;
    //下次执行时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date nextExceTime;
    //执行次数
    private Long exceCount;
}
