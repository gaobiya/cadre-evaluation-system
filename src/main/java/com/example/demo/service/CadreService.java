package com.example.demo.service;

import com.example.demo.entity.Cadre;
import com.example.demo.entity.CadreEvaluationResult;
import com.example.demo.entity.PageResult;

import java.util.List;

/**
 * 中层干部服务接口
 */
public interface CadreService {
    /**
     * 获取所有干部列表
     * @return 干部列表
     */
    List<Cadre> listAll();
    
    /**
     * 分页获取干部列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Cadre> listByPage(int pageNum, int pageSize);
    
    /**
     * 根据ID获取干部信息
     * @param id 干部ID
     * @return 干部信息
     */
    Cadre getById(Long id);
    
    /**
     * 添加干部
     * @param cadre 干部信息
     * @return 是否成功
     */
    boolean add(Cadre cadre);
    
    /**
     * 更新干部信息
     * @param cadre 干部信息
     * @return 是否成功
     */
    boolean update(Cadre cadre);
    
    /**
     * 删除干部
     * @param id 干部ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 获取干部测评结果
     * @param cadreId 干部ID
     * @return 测评结果
     */
    CadreEvaluationResult getEvaluationResult(Long cadreId);
    
    /**
     * 获取所有干部测评结果
     * @return 测评结果列表
     */
    List<CadreEvaluationResult> listAllEvaluationResults();
    
    /**
     * 分页获取干部测评结果
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 测评结果分页列表
     */
    PageResult<CadreEvaluationResult> listEvaluationResultsByPage(int pageNum, int pageSize);
    
    /**
     * 获取干部的评价人数
     * @param cadreId 干部ID
     * @return 评价人数
     */
    int getEvaluatorCount(Long cadreId);
    
    /**
     * 获取干部的平均评分
     * @param cadreId 干部ID
     * @return 平均评分
     */
    double getAverageScore(Long cadreId);
    
    /**
     * 获取干部某个类别的平均评分
     * @param cadreId 干部ID
     * @param category 评价类别
     * @return 平均评分
     */
    double getAverageScore(Long cadreId, String category);
} 