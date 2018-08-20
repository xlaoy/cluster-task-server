package com.task.server.repository;

import com.task.server.entity.SecheduledRegisterLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2018/8/16 0016.
 */
@Repository
public interface ISecheduledRegisterLogRepository extends MongoRepository<SecheduledRegisterLog, String> {


}
