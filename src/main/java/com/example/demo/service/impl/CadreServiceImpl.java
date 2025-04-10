package com.example.demo.service.impl;

import com.example.demo.entity.Cadre;
import com.example.demo.entity.CadreEvaluationResult;
import com.example.demo.entity.Evaluation;
import com.example.demo.entity.EvaluationCriteria;
import com.example.demo.entity.EvaluationPeriod;
import com.example.demo.entity.PageResult;
import com.example.demo.repository.CadreRepository;
import com.example.demo.repository.EvaluationCriteriaRepository;
import com.example.demo.repository.EvaluationRepository;
import com.example.demo.service.CadreService;
import com.example.demo.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.StringJoiner;

/**
 * 中层干部服务实现类
 */
@Service
public class CadreServiceImpl implements CadreService {
    
    @Autowired
    private CadreRepository cadreRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private EvaluationCriteriaRepository criteriaRepository;
    
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public List<Cadre> listAll() {
        return cadreRepository.findAll();
    }

    @Override
    public Cadre getById(Long id) {
        return cadreRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public boolean add(Cadre cadre) {
        if (cadre == null) {
            return false;
        }
        
        // 设置时间
        Date now = new Date();
        cadre.setCreateTime(now);
        cadre.setUpdateTime(now);
        
        cadreRepository.save(cadre);
        return true;
    }

    @Override
    @Transactional
    public boolean update(Cadre cadre) {
        if (cadre == null || cadre.getId() == null) {
            return false;
        }
        
        Optional<Cadre> existingCadreOpt = cadreRepository.findById(cadre.getId());
        if (!existingCadreOpt.isPresent()) {
            return false;
        }
        
        Cadre existingCadre = existingCadreOpt.get();
        
        // 更新数据，保留原始创建时间
        if (cadre.getName() != null) {
            existingCadre.setName(cadre.getName());
        }
        if (cadre.getDepartment() != null) {
            existingCadre.setDepartment(cadre.getDepartment());
        }
        if (cadre.getPosition() != null) {
            existingCadre.setPosition(cadre.getPosition());
        }
        
        existingCadre.setUpdateTime(new Date());
        
        cadreRepository.save(existingCadre);
        return true;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        // 检查干部是否存在
        if (!cadreRepository.existsById(id)) {
            return false;
        }
        
        // 删除与该干部相关的所有测评记录
        evaluationRepository.deleteByCadreId(id);
        
        // 删除干部
        cadreRepository.deleteById(id);
        return true;
    }

    @Override
    public CadreEvaluationResult getEvaluationResult(Long cadreId) {
        // 获取干部信息
        Cadre cadre = cadreRepository.findById(cadreId).orElse(null);
        if (cadre == null) {
            return null;
        }
        
        CadreEvaluationResult result = new CadreEvaluationResult();
        result.setCadreId(cadre.getId());
        result.setCadreName(cadre.getName());
        result.setDepartment(cadre.getDepartment());
        result.setPosition(cadre.getPosition());
        
        // 获取评价人数量
        Integer evaluatorCount = evaluationRepository.countEvaluatorsByCadreId(cadreId);
        result.setEvaluatorCount(evaluatorCount != null ? evaluatorCount : 0);
        
        // 获取总平均分
        Double totalScore = evaluationRepository.getAverageScoreByCadreId(cadreId);
        result.setTotalScore(totalScore != null ? totalScore : 0.0);
        
        // 获取各项指标平均分
        Double deScore = evaluationRepository.getAverageScoreByCadreIdAndCategory(cadreId, "德");
        result.setDeScore(deScore != null ? deScore : 0.0);
        
        Double nengScore = evaluationRepository.getAverageScoreByCadreIdAndCategory(cadreId, "能");
        result.setNengScore(nengScore != null ? nengScore : 0.0);
        
        Double qinScore = evaluationRepository.getAverageScoreByCadreIdAndCategory(cadreId, "勤");
        result.setQinScore(qinScore != null ? qinScore : 0.0);
        
        Double jiScore = evaluationRepository.getAverageScoreByCadreIdAndCategory(cadreId, "绩");
        result.setJiScore(jiScore != null ? jiScore : 0.0);
        
        Double lianScore = evaluationRepository.getAverageScoreByCadreIdAndCategory(cadreId, "廉");
        result.setLianScore(lianScore != null ? lianScore : 0.0);
        
        // 默认评语
        result.setComments("工作认真负责，团队协作能力强");
        
        return result;
    }

    @Override
    public List<CadreEvaluationResult> listAllEvaluationResults() {
        List<CadreEvaluationResult> results = new ArrayList<>();
        
        // 获取所有干部的测评结果
        List<Cadre> cadres = cadreRepository.findAll();
        for (Cadre cadre : cadres) {
            CadreEvaluationResult result = getEvaluationResult(cadre.getId());
            if (result != null) {
                results.add(result);
            }
        }
        
        return results;
    }
    
    @Override
    public PageResult<CadreEvaluationResult> listEvaluationResultsByPage(int pageNum, int pageSize) {
        // 页码从0开始，所以需要减1
        pageNum = Math.max(0, pageNum - 1);
        pageSize = Math.max(1, pageSize);
        
        // 创建分页请求对象
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        
        // 获取分页的干部列表
        Page<Cadre> cadrePage = cadreRepository.findAll(pageable);
        
        // 将每个干部转换为评价结果
        List<CadreEvaluationResult> resultList = new ArrayList<>();
        for (Cadre cadre : cadrePage.getContent()) {
            CadreEvaluationResult result = getEvaluationResult(cadre.getId());
            if (result != null) {
                resultList.add(result);
            }
        }
        
        // 构建分页结果
        return new PageResult<>(pageNum + 1, pageSize, cadrePage.getTotalElements(), resultList);
    }
    
    /**
     * 初始化测试干部数据
     */
    @Transactional
    public void initCadreData() {
        if (cadreRepository.count() == 0) {
            // 添加一些测试数据
            Cadre cadre1 = new Cadre();
            cadre1.setName("张三");
            cadre1.setDepartment("人力资源部");
            cadre1.setPosition("部门经理");
            cadre1.setCreateTime(new Date());
            cadre1.setUpdateTime(new Date());
            
            Cadre cadre2 = new Cadre();
            cadre2.setName("李四");
            cadre2.setDepartment("财务部");
            cadre2.setPosition("部门主管");
            cadre2.setCreateTime(new Date());
            cadre2.setUpdateTime(new Date());
            
            Cadre cadre3 = new Cadre();
            cadre3.setName("王五");
            cadre3.setDepartment("技术部");
            cadre3.setPosition("部门经理");
            cadre3.setCreateTime(new Date());
            cadre3.setUpdateTime(new Date());
            
            Cadre cadre4 = new Cadre();
            cadre4.setName("赵六");
            cadre4.setDepartment("销售部");
            cadre4.setPosition("销售总监");
            cadre4.setCreateTime(new Date());
            cadre4.setUpdateTime(new Date());
            
            Cadre cadre5 = new Cadre();
            cadre5.setName("钱七");
            cadre5.setDepartment("市场部");
            cadre5.setPosition("市场总监");
            cadre5.setCreateTime(new Date());
            cadre5.setUpdateTime(new Date());
            
            Cadre cadre6 = new Cadre();
            cadre6.setName("孙八");
            cadre6.setDepartment("产品部");
            cadre6.setPosition("产品经理");
            cadre6.setCreateTime(new Date());
            cadre6.setUpdateTime(new Date());
            
            Cadre cadre7 = new Cadre();
            cadre7.setName("周九");
            cadre7.setDepartment("研发部");
            cadre7.setPosition("技术总监");
            cadre7.setCreateTime(new Date());
            cadre7.setUpdateTime(new Date());
            
            Cadre cadre8 = new Cadre();
            cadre8.setName("吴十");
            cadre8.setDepartment("行政部");
            cadre8.setPosition("行政主管");
            cadre8.setCreateTime(new Date());
            cadre8.setUpdateTime(new Date());
            
            cadreRepository.save(cadre1);
            cadreRepository.save(cadre2);
            cadreRepository.save(cadre3);
            cadreRepository.save(cadre4);
            cadreRepository.save(cadre5);
            cadreRepository.save(cadre6);
            cadreRepository.save(cadre7);
            cadreRepository.save(cadre8);
        }
    }
    
    /**
     * 初始化干部数据
     * 供DataInitializer调用
     */
    @Transactional
    public void initCadres() {
        initCadreData();
    }
    
    @Override
    public int getEvaluatorCount(Long cadreId) {
        Integer count = evaluationRepository.countEvaluatorsByCadreId(cadreId);
        return count != null ? count : 0;
    }
    
    @Override
    public double getAverageScore(Long cadreId) {
        Double avgScore = evaluationRepository.getAverageScoreByCadreId(cadreId);
        return avgScore != null ? avgScore : 0.0;
    }
    
    @Override
    public double getAverageScore(Long cadreId, String category) {
        Double avgScore = evaluationRepository.getAverageScoreByCadreIdAndCategory(cadreId, category);
        return avgScore != null ? avgScore : 0.0;
    }

    @Override
    public PageResult<Cadre> listByPage(int pageNum, int pageSize) {
        // 页码从0开始，所以需要减1
        pageNum = Math.max(0, pageNum - 1);
        pageSize = Math.max(1, pageSize);
        
        // 创建分页请求对象，按ID降序排序
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        
        // 执行分页查询
        Page<Cadre> page = cadreRepository.findAll(pageable);
        
        // 构建分页结果
        return new PageResult<>(pageNum + 1, pageSize, page.getTotalElements(), page.getContent());
    }

    /**
     * 计算干部的评价得分
     */
    private CadreEvaluationResult calculateEvaluationResult(Long cadreId) {
        CadreEvaluationResult result = new CadreEvaluationResult();
        result.setCadreId(cadreId);
        
        // 获取所有评价记录
        List<Evaluation> evaluations = evaluationRepository.findByCadreId(cadreId);
        
        // 获取干部信息
        Cadre cadre = cadreRepository.findById(cadreId).orElse(null);
        if (cadre == null) {
            return null;
        }
        
        result.setCadreName(cadre.getName());
        result.setDepartment(cadre.getDepartment());
        result.setPosition(cadre.getPosition());
        
        // 统计每个类别的得分和权重
        Map<String, Double> categoryScores = new HashMap<>();    // 类别得分
        Map<String, Integer> categoryWeights = new HashMap<>();  // 类别权重
        Map<String, Integer> categoryCount = new HashMap<>();    // 每个类别的评价数量
        
        Set<String> evaluators = new HashSet<>(); // 去重的评价人
        List<String> comments = new ArrayList<>(); // 收集评语
        
        // 初始化类别权重
        categoryWeights.put("德", 15);
        categoryWeights.put("能", 20);
        categoryWeights.put("勤", 15);
        categoryWeights.put("绩", 40);
        categoryWeights.put("廉", 10);
        
        // 初始化类别得分和计数
        for (String category : categoryWeights.keySet()) {
            categoryScores.put(category, 0.0);
            categoryCount.put(category, 0);
        }
        
        // 遍历所有评价记录
        for (Evaluation evaluation : evaluations) {
            // 记录评价人
            if (evaluation.getEvaluatorName() != null) {
                evaluators.add(evaluation.getEvaluatorName());
            }
            
            // 收集评语
            if (evaluation.getComments() != null && !evaluation.getComments().isEmpty()) {
                comments.add(evaluation.getComments());
            }
            
            // 根据评价指标的类别分类得分
            Optional<EvaluationCriteria> criteriaOpt = criteriaRepository.findById(evaluation.getCriteriaId());
            if (criteriaOpt.isPresent()) {
                EvaluationCriteria criteria = criteriaOpt.get();
                String category = criteria.getCategory();
                if (categoryScores.containsKey(category)) {
                    // 累加得分（考虑指标权重）
                    double weightedScore = evaluation.getScore() * criteria.getWeight();
                    categoryScores.put(category, categoryScores.get(category) + weightedScore);
                    categoryCount.put(category, categoryCount.get(category) + criteria.getWeight());
                }
            }
        }
        
        // 计算各类别平均分
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;
        
        for (String category : categoryWeights.keySet()) {
            double categoryScore = 0.0;
            if (categoryCount.get(category) > 0) {
                categoryScore = categoryScores.get(category) / categoryCount.get(category);
            }
            
            // 设置各维度得分
            switch (category) {
                case "德":
                    result.setDeScore(categoryScore);
                    break;
                case "能":
                    result.setNengScore(categoryScore);
                    break;
                case "勤":
                    result.setQinScore(categoryScore);
                    break;
                case "绩":
                    result.setJiScore(categoryScore);
                    break;
                case "廉":
                    result.setLianScore(categoryScore);
                    break;
            }
            
            // 累加总分（考虑维度权重）
            double categoryWeight = categoryWeights.get(category);
            totalWeightedScore += categoryScore * categoryWeight;
            totalWeight += categoryWeight;
        }
        
        // 计算总分
        double totalScore = totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
        result.setTotalScore(totalScore);
        
        // 设置评价人数
        result.setEvaluatorCount(evaluators.size());
        
        // 设置评语（取前3条，用分号分隔）
        if (!comments.isEmpty()) {
            int commentsToShow = Math.min(comments.size(), 3);
            String joinedComments = String.join("; ", comments.subList(0, commentsToShow));
            if (comments.size() > commentsToShow) {
                joinedComments += "; ...等" + comments.size() + "条评语";
            }
            result.setComments(joinedComments);
        }
        
        return result;
    }
} 