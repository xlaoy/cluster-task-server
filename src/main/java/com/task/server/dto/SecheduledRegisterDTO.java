package com.task.server.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Data
public class SecheduledRegisterDTO {
    //服务名称
    private String serviceName;
    //ip端口
    private String hostAndPort;
    //
    private List<SecheduledInfo> secheduledInfoList;

    @Data
    public static class SecheduledInfo {
        //类名称
        private String className;
        //执行时间
        private String cron;
    }

}
