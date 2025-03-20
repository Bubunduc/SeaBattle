package com.seabattle.Course_work.services;
import com.seabattle.Course_work.models.Result;
import com.seabattle.Course_work.repositories.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ResultService {

    private final ResultRepository resultRepository;

    @Autowired
    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }
    @Transactional
    public void saveResult(String winner, String loser) {
        Result result = new Result(winner, loser, LocalDateTime.now());
        resultRepository.save(result);
    }
}
