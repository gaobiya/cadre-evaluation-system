package com.example.demo.repository;

import com.example.demo.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测评记录数据库仓库接口
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    
    /**
     * 根据干部ID查询测评记录
     * @param cadreId 干部ID
     * @return 测评记录列表
     */
    List<Evaluation> findByCadreId(Long cadreId);
    
    /**
     * 根据干部ID和评价人信息查询测评记录
     * @param cadreId 干部ID
     * @param evaluatorName 评价人姓名
     * @param evaluatorDept 评价人部门
     * @return 测评记录列表
     */
    List<Evaluation> findByCadreIdAndEvaluatorNameAndEvaluatorDept(Long cadreId, String evaluatorName, String evaluatorDept);
    
    /**
     * 根据干部ID删除测评记录
     * @param cadreId 干部ID
     */
    void deleteByCadreId(Long cadreId);
    
    /**
     * 统计指定干部的评价人数量
     * @param cadreId 干部ID
     * @return 评价人数量
     */
    @Query("SELECT COUNT(DISTINCT e.evaluatorName) FROM Evaluation e WHERE e.cadreId = :cadreId")
    Integer countEvaluatorsByCadreId(@Param("cadreId") Long cadreId);
    
    /**
     * 获取指定干部的指定类别测评指标的加权平均分
     * 考虑每个指标的权重进行计算
     * @param cadreId 干部ID
     * @param category 类别
     * @return 加权平均分
     */
    @Query("SELECT SUM(e.score * c.weight) / SUM(c.weight) " +
           "FROM Evaluation e JOIN EvaluationCriteria c ON e.criteriaId = c.id " +
           "WHERE e.cadreId = :cadreId AND c.category = :category")
    Double getAverageScoreByCadreIdAndCategory(@Param("cadreId") Long cadreId, @Param("category") String category);
    
    /**
     * 获取指定干部的测评总加权平均分
     * 考虑所有指标的权重进行计算
     * @param cadreId 干部ID
     * @return 总加权平均分
     */
    @Query("SELECT SUM(e.score * c.weight) / SUM(c.weight) " +
           "FROM Evaluation e JOIN EvaluationCriteria c ON e.criteriaId = c.id " +
           "WHERE e.cadreId = :cadreId")
    Double getAverageScoreByCadreId(@Param("cadreId") Long cadreId);
    
    /**
     * 检查用户在指定周期内是否已对指定干部进行评价
     * @param cadreId 干部ID
     * @param userId 用户ID
     * @param periodId 周期ID
     * @return 存在评价的数量
     */
    Integer countByCadreIdAndUserIdAndPeriodId(Long cadreId, Long userId, Long periodId);
    
    /**
     * 查询用户在当前周期内对指定干部的评价
     * @param cadreId 干部ID
     * @param userId 用户ID
     * @param periodId 周期ID
     * @return 评价列表
     */
    List<Evaluation> findByCadreIdAndUserIdAndPeriodId(Long cadreId, Long userId, Long periodId);
} 