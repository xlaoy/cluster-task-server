package com.task.server.service;

import com.task.server.entity.BlackIP;
import com.task.server.repository.IBlackIPRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/8/23 0023.
 */
@Slf4j
@Component
public class ClientServiceChoose {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private IBlackIPRepository blackIPRepository;

    public ServiceInstance getClientServiceInstance(String serviceName) {
        List<ServiceInstance> serviceList = getClientServiceList(serviceName);
        int index = new Random().nextInt(serviceList.size());
        return serviceList.get(index);
    }

    public List<ServiceInstance> getClientServiceList(String serviceName) {
        if(StringUtils.isEmpty(serviceName)) {
            log.error("服务名称为空");
            throw new RuntimeException("服务名称为空");
        }
        serviceName = serviceName.toUpperCase();
        List<ServiceInstance> returnList = new ArrayList<>();
        List<ServiceInstance> serviceList;
        try {
            serviceList = discoveryClient.getInstances(serviceName);
        } catch (Exception e) {
            throw new RuntimeException(serviceName + " 选择服务实例异常");
        }
        if(CollectionUtils.isEmpty(serviceList)) {
            throw new RuntimeException(serviceName + "没有可用实例");
        }
        List<BlackIP> blackIPList = blackIPRepository.findByServiceName(serviceName);
        for(ServiceInstance instance : serviceList) {
            if(!CollectionUtils.isEmpty(blackIPList)) {
                List<BlackIP> blist = blackIPList.stream().filter(b ->
                        b.getHost().equals(String.valueOf(instance.getHost()))
                                && b.getPort().equals(String.valueOf(instance.getPort()))
                ).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(blist)) {
                    returnList.add(instance);
                }
            } else {
                returnList.add(instance);
            }
        }
        if(CollectionUtils.isEmpty(returnList)) {
            throw new RuntimeException(serviceName + " 黑名单过滤后没有可用实例");
        }
        return returnList;
    }
}
