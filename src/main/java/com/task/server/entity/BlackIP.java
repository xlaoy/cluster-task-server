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
@Document(collection = "black_ip")
public class BlackIP {

    @Id
    private String id;
    //
    private String serviceName;
    //
    private String host;
    //
    private String port;
    //创建时间
    @JsonFormat(locale = BeanConfig.LOCALE, timezone = BeanConfig.TIMEZONE, pattern = BeanConfig.YYYY_MM_DD_HH_MM_SS)
    private Date createTime;
}
