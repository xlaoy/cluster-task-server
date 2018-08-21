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
@Document(collection = "secheduled_execute_log")
public class TaskExecuteLog {

    public static final String SEND_REQUEST_SUCCESS= "send_request_success";
    public static final String SEND_REQUEST_FAILURE = "send_request_failure";
    public static final String EXECUTE_SUCCESS = "execute_success";
    public static final String EXECUTE_FAILURE = "execute_failure";

    public static final String SECHEDULED = "secheduled";
    public static final String DELAY = "delay";

    @Id
    private String id;
    //服务名称
    private String taskId;
    //分片id
    private String pieceId;
    //任务类型
    private String taskType;
    //目标机器
    private String targetHostPort;
    //执行状态
    private String status;
    //
    private Long exceCount;
    //开始时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date beginTime;
    //发送请求时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date sendRequestTime;
    //请求体
    private String requestBody;
    //收到响应时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date receiveResponseTime;
    //执行结果反馈时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date feedbackTime;
    //执行结果
    private String result;

}
