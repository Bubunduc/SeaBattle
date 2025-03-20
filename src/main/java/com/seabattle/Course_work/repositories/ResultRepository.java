package com.seabattle.Course_work.repositories;

import com.seabattle.Course_work.models.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByWinnerOrLoser(String winner, String loser);
}
