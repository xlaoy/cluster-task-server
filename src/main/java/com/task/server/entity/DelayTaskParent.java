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
public class DelayTaskParent {

    public static final String WAITING = "waiting";//待执行
    public static final String EXECUTEING = "executeing";//执行中
    public static final String CANCEL = "cancel";//取消
    public static final String FINISH = "finish";//完成
    public static final String FEEDBACK_TIMEOUT = "feedback_timeout";//超时反馈

    //注册服务名称
    protected String registerServiceName;
    //注册ip端口
    protected String registerHostAndPort;
    //执行服务名称
    protected String executeServiceName;
    //业务名称
    protected String bizName;
    //业务参数
    protected String bizParameters;
    //任务状态
    protected String status;
    //创建时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    protected Date createTime;
    //执行时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    protected Date executeTime;
    //执行次数
    protected Long exceCount;
    //备注
    protected String remark;
}
