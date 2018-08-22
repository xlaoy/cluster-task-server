package com.task.server.controller;

import com.task.server.dto.SecheduledTaskPieceDTO;
import com.task.server.entity.SecheduledTaskInfo;
import com.task.server.repository.ISecheduledTaskInfoRepository;
import com.task.server.repository.ISecheduledTaskPieceRepository;
import com.task.server.service.SecheduledTaskPieceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@RestController
public class SechedulePieceController {

    @Autowired
    private SecheduledTaskPieceService taskPieceService;

    @PostMapping("/task_server/add_secheduled_piece")
    public void addSecheduledPiece(@RequestBody SecheduledTaskPieceDTO pieceDTO) {
        taskPieceService.addSecheduledPiece(pieceDTO);
    }

}
