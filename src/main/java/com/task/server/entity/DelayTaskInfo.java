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
@Document(collection = "delay_task_info")
public class DelayTaskInfo {

    public static final String YES = "yes";
    public static final String NOT = "not";

    public static final String NORMAL = "normal";//正常
    public static final String CANCEL = "cancel";//取消
    public static final String FINISH = "finish";//完成

    @Id
    private String id;
    //注册服务名称
    private String registerServiceName;
    //注册ip端口
    private String registerHostAndPort;
    //执行服务名称
    private String executeServiceName;
    //业务名称
    private String bizName;
    //业务参数
    private String bizParameters;
    //能否执行
    private String canExecute;
    //任务状态
    private String status;
    //创建时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date createTime;
    //执行时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date executeTime;
    //执行次数
    private Integer exceCount;
    //备注
    private String remark;
}
