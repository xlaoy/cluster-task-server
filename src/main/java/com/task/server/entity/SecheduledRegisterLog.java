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
@Document(collection = "secheduled_register_log")
public class SecheduledRegisterLog {

    public static final String RECEIVE_REQUEST = "receive_request";
    public static final String REGISTER_SUCCESS = "register_success";
    public static final String REGISTER_FAILURE = "register_failure";

    @Id
    private String id;
    //服务名称
    private String serviceName;
    //ip端口
    private String hostAndPort;
    //注册任务列表
    private String secheduledInfoList;
    //创建时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date createTime;
    //注册状态
    private String status;

}
