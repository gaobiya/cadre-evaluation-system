package com.example.demo.repository;

import com.example.demo.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测评指标数据库仓库接口
 */
@Repository
public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long> {
    
    /**
     * 按显示顺序排序查询所有测评指标
     * @return 测评指标列表
     */
    List<EvaluationCriteria> findAllByOrderByDisplayOrderAsc();
    
    /**
     * 根据类别查询测评指标
     * @param category 类别
     * @return 测评指标列表
     */
    List<EvaluationCriteria> findByCategory(String category);
} 