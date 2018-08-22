package com.task.server.service;

import com.task.server.dto.SecheduledTaskPieceDTO;
import com.task.server.entity.SecheduledTaskPiece;
import com.task.server.repository.ISecheduledTaskPieceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by Administrator on 2018/8/22 0022.
 */
@Component
public class SecheduledTaskPieceService {

    @Autowired
    private ISecheduledTaskPieceRepository taskPieceRepository;


    public void addSecheduledPiece(SecheduledTaskPieceDTO pieceDTO) {
        Assert.notNull(pieceDTO.getTaskId(), "taskId不能为空");
        Assert.notNull(pieceDTO.getParameters(), "parameters不能为空");
        SecheduledTaskPiece taskPiece = new SecheduledTaskPiece();
        taskPiece.setStatus(SecheduledTaskPiece.NORMAL);
        taskPiece.setParameters(pieceDTO.getParameters());
        taskPiece.setTaskId(pieceDTO.getTaskId());
        taskPieceRepository.save(taskPiece);
    }
}
