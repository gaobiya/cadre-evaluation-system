package com.example.demo.service;

import com.example.demo.entity.Evaluation;
import com.example.demo.entity.EvaluationCriteria;
import com.example.demo.entity.EvaluationPeriod;
import com.example.demo.entity.PageResult;

import java.util.List;
import java.util.Optional;

/**
 * 测评服务接口
 */
public interface EvaluationService {
    /**
     * 获取所有测评指标
     * @return 测评指标列表
     */
    List<EvaluationCriteria> listAllCriteria();
    
    /**
     * 获取指定干部的测评记录
     * @param cadreId 干部ID
     * @return 测评记录列表
     */
    List<Evaluation> listByCadreId(Long cadreId);
    
    /**
     * 获取指定评价人对干部的测评记录
     * @param cadreId 干部ID
     * @param evaluatorName 评价人姓名
     * @param evaluatorDept 评价人部门
     * @return 测评记录列表
     */
    List<Evaluation> listByEvaluator(Long cadreId, String evaluatorName, String evaluatorDept);
    
    /**
     * 添加测评记录
     * @param evaluation 测评记录
     * @return 是否成功
     */
    boolean addEvaluation(Evaluation evaluation);
    
    /**
     * 批量添加测评记录
     * @param evaluations 测评记录列表
     * @return 是否成功
     */
    boolean batchAddEvaluations(List<Evaluation> evaluations);
    
    /**
     * 删除测评记录
     * @param id 测评记录ID
     * @return 是否成功
     */
    boolean deleteEvaluation(Long id);
    
    /**
     * 根据干部ID和评价人信息删除测评记录
     * @param cadreId 干部ID
     * @param evaluatorName 评价人姓名
     * @param evaluatorDept 评价人部门
     * @return 是否成功
     */
    boolean deleteEvaluationByEvaluator(Long cadreId, String evaluatorName, String evaluatorDept);

    /**
     * 获取单个评价指标
     * @param id 评价指标ID
     * @return 评价指标
     */
    EvaluationCriteria getCriteriaById(Long id);
    
    /**
     * 获取当前有效的测评周期
     * @return 测评周期，如果没有则返回null
     */
    EvaluationPeriod getCurrentPeriod();
    
    /**
     * 创建新的测评周期
     * @param period 测评周期
     * @return 是否成功
     */
    boolean createPeriod(EvaluationPeriod period);
    
    /**
     * 更新测评周期
     * @param period 测评周期
     * @return 是否成功
     */
    boolean updatePeriod(EvaluationPeriod period);
    
    /**
     * 根据ID查找测评周期
     * @param id 测评周期ID
     * @return 测评周期Optional对象
     */
    Optional<EvaluationPeriod> findPeriodById(Long id);
    
    /**
     * 获取所有测评周期
     * @return 测评周期列表
     */
    List<EvaluationPeriod> listAllPeriods();
    
    /**
     * 分页获取测评周期
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<EvaluationPeriod> listPeriodsByPage(int pageNum, int pageSize);
    
    /**
     * 检查用户在当前周期内是否已经评价过指定干部
     * @param cadreId 干部ID
     * @param userId 用户ID
     * @return 如果已评价过返回true，否则返回false
     */
    boolean hasUserEvaluatedCadreInCurrentPeriod(Long cadreId, Long userId);
} 