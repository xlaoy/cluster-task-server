package com.task.server.repository;

import com.task.server.entity.BlackIP;
import com.task.server.entity.TaskExecuteLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2018/8/16 0016.
 */
@Repository
public interface IBlackIPRepository extends MongoRepository<BlackIP, String> {

    List<BlackIP> findByServiceNameAndHostAndPort(String serviceName, String host, String port);

    List<BlackIP> findByServiceName(String serviceName);

}
