package com.task.server.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Data
public class DelayRegisterDTO {

    //注册服务名称
    private String registerServiceName;
    //注册ip端口
    private String registerHostAndPort;
    //
    private List<DelayInfo> delayInfoList;

    @Data
    @ToString
    public static class DelayInfo {
        //执行服务名称
        private String executeServiceName;
        //业务名称
        private String bizName;
        //业务参数
        private String bizParameters;
        //执行时间
        private Date executeTime;
    }

}
