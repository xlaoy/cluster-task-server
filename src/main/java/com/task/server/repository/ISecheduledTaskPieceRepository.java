package com.task.server.repository;

import com.task.server.entity.SecheduledTaskPiece;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2018/8/16 0016.
 */
@Repository
public interface ISecheduledTaskPieceRepository extends MongoRepository<SecheduledTaskPiece, String> {

    List<SecheduledTaskPiece> findByTaskId(String taskId);

}
