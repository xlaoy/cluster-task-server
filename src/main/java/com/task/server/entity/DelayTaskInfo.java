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

    public static final String WAITING = "waiting";//待执行
    public static final String EXECUTEING = "executeing";//执行中
    public static final String CANCEL = "cancel";//取消
    public static final String FINISH = "finish";//完成
    public static final String FEEDBACK_TIMEOUT = "feedback_timeout";//超时反馈

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
    //任务状态
    private String status;
    //创建时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date createTime;
    //执行时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date executeTime;
    //执行次数
    private Long exceCount;
    //备注
    private String remark;
}
