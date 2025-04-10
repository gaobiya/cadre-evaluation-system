package com.example.demo.repository;

import com.example.demo.entity.EvaluationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 测评周期数据库仓库接口
 */
@Repository
public interface EvaluationPeriodRepository extends JpaRepository<EvaluationPeriod, Long> {
    
    /**
     * 查询当前激活的测评周期
     * @return 测评周期列表
     */
    List<EvaluationPeriod> findByIsActiveTrue();
    
    /**
     * 查询当前有效的测评周期（当前时间在开始和结束时间之间的周期）
     * @param currentTime 当前时间
     * @return 测评周期列表
     */
    @Query("SELECT p FROM EvaluationPeriod p WHERE p.isActive = true AND p.startTime <= ?1 AND p.endTime >= ?1")
    List<EvaluationPeriod> findCurrentActivePeriods(Date currentTime);
    
    /**
     * 查询最新的测评周期
     * @return 测评周期
     */
    Optional<EvaluationPeriod> findTopByOrderByEndTimeDesc();
} 