package com.example.demo.service.impl;

import com.example.demo.entity.Evaluation;
import com.example.demo.entity.EvaluationCriteria;
import com.example.demo.entity.EvaluationPeriod;
import com.example.demo.entity.PageResult;
import com.example.demo.repository.EvaluationCriteriaRepository;
import com.example.demo.repository.EvaluationPeriodRepository;
import com.example.demo.repository.EvaluationRepository;
import com.example.demo.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 测评服务实现类
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {
    
    @Autowired
    private EvaluationCriteriaRepository criteriaRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private EvaluationPeriodRepository periodRepository;

    @Override
    public List<EvaluationCriteria> listAllCriteria() {
        return criteriaRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    public List<Evaluation> listByCadreId(Long cadreId) {
        if (cadreId == null) {
            return List.of();
        }
        
        return evaluationRepository.findByCadreId(cadreId);
    }

    @Override
    public List<Evaluation> listByEvaluator(Long cadreId, String evaluatorName, String evaluatorDept) {
        if (cadreId == null || evaluatorName == null || evaluatorDept == null) {
            return List.of();
        }
        
        return evaluationRepository.findByCadreIdAndEvaluatorNameAndEvaluatorDept(
            cadreId, evaluatorName, evaluatorDept);
    }

    @Override
    @Transactional
    public boolean addEvaluation(Evaluation evaluation) {
        if (evaluation == null) {
            return false;
        }
        
        // 检查是否在测评周期内
        EvaluationPeriod currentPeriod = getCurrentPeriod();
        if (currentPeriod == null) {
            return false; // 当前没有有效的测评周期
        }
        
        // 设置周期ID
        evaluation.setPeriodId(currentPeriod.getId());
        
        // 设置时间
        if (evaluation.getEvaluationTime() == null) {
            evaluation.setEvaluationTime(new Date());
        }
        
        evaluationRepository.save(evaluation);
        return true;
    }

    @Override
    @Transactional
    public boolean batchAddEvaluations(List<Evaluation> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            return false;
        }
        
        // 检查是否在测评周期内
        EvaluationPeriod currentPeriod = getCurrentPeriod();
        if (currentPeriod == null) {
            return false; // 当前没有有效的测评周期
        }
        
        // 如果用户已经对该干部进行过评价，则不允许重复评价
        if (!evaluations.isEmpty()) {
            Long cadreId = evaluations.get(0).getCadreId();
            Long userId = evaluations.get(0).getUserId();
            
            if (cadreId != null && userId != null && hasUserEvaluatedCadreInCurrentPeriod(cadreId, userId)) {
                return false;
            }
        }
        
        Date now = new Date();
        for (Evaluation evaluation : evaluations) {
            // 设置周期ID
            evaluation.setPeriodId(currentPeriod.getId());
            
            if (evaluation.getEvaluationTime() == null) {
                evaluation.setEvaluationTime(now);
            }
        }
        
        evaluationRepository.saveAll(evaluations);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteEvaluation(Long id) {
        if (id == null) {
            return false;
        }
        
        if (!evaluationRepository.existsById(id)) {
            return false;
        }
        
        evaluationRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteEvaluationByEvaluator(Long cadreId, String evaluatorName, String evaluatorDept) {
        if (cadreId == null || evaluatorName == null || evaluatorName.isEmpty()) {
            return false;
        }
        
        List<Evaluation> evaluations = evaluationRepository.findByCadreIdAndEvaluatorNameAndEvaluatorDept(
                cadreId, evaluatorName, evaluatorDept);
        
        evaluationRepository.deleteAll(evaluations);
        return true;
    }
    
    /**
     * 初始化测评数据
     */
    @Transactional
    public void initEvaluationData() {
        // 只有在没有任何评价数据的情况下初始化
        if (evaluationRepository.count() == 0) {
            // 获取当前有效的测评周期
            EvaluationPeriod currentPeriod = getCurrentPeriod();
            if (currentPeriod == null) {
                return;
            }
            
            // 为前3个干部添加一些评价数据
            for (long cadreId = 1; cadreId <= 3; cadreId++) {
                // 每个干部由5个用户进行评价
                for (long userId = 1; userId <= 5; userId++) {
                    // 生成这个用户对这个干部的评语
                    String comment = String.format(
                        "干部%d在工作中表现积极，工作态度认真负责，具有较强的组织协调能力和团队合作精神。在德、能、勤、绩、廉等方面都有良好表现。",
                        cadreId
                    );
                    
                    // 获取所有评价指标
                    List<EvaluationCriteria> criteriaList = criteriaRepository.findAll();
                    
                    for (EvaluationCriteria criteria : criteriaList) {
                        Evaluation evaluation = new Evaluation();
                        evaluation.setCadreId(cadreId);
                        evaluation.setCriteriaId(criteria.getId());
                        evaluation.setUserId(userId);
                        evaluation.setPeriodId(currentPeriod.getId());
                        
                        // 生成6-10之间的随机分数
                        int score = 6 + (int)(Math.random() * 5);
                        evaluation.setScore(score);
                        
                        // 设置评价人信息
                        evaluation.setEvaluatorName("用户" + userId);
                        evaluation.setEvaluatorDept("部门" + (userId % 3 + 1));
                        
                        // 只在第一个指标记录中设置评语
                        if (criteria.getDisplayOrder() == 1) {
                            evaluation.setComments(comment);
                        }
                        
                        // 设置评价时间
                        Date evaluationTime = new Date();
                        evaluation.setEvaluationTime(evaluationTime);
                        
                        // 保存评价
                        evaluationRepository.save(evaluation);
                    }
                }
            }
        }
    }
    
    /**
     * 初始化测评指标数据
     */
    @Transactional
    public void initEvaluationCriteria() {
        if (criteriaRepository.count() == 0) {
            // 添加测评指标 - 按照"德能勤绩廉"五个维度

            // 德 (15分)
            EvaluationCriteria criteria1 = new EvaluationCriteria();
            criteria1.setCode("D001");
            criteria1.setName("政治素质与道德品质");
            criteria1.setDescription("政治立场坚定，热爱教育事业，遵守职业道德，遵守学校规章制度，办事公道，坚持原则，为人师表。");
            criteria1.setWeight(5);
            criteria1.setCategory("德");
            criteria1.setDisplayOrder(1);
            
            EvaluationCriteria criteria2 = new EvaluationCriteria();
            criteria2.setCode("D002");
            criteria2.setName("工作作风");
            criteria2.setDescription("谦虚谨慎，团结同志，坚持民主集中制，不独断专行，善于发挥团队作用，严于律己，秉公办事，虚心纳谏，善心帮助他人。");
            criteria2.setWeight(10);
            criteria2.setCategory("德");
            criteria2.setDisplayOrder(2);
            
            // 能 (20分)
            EvaluationCriteria criteria3 = new EvaluationCriteria();
            criteria3.setCode("N001");
            criteria3.setName("工作思路");
            criteria3.setDescription("能准确把握中心工作重点，制定符合实际的分管工作计划，思路清晰，措施有力，各项分管工作有主题，有学习，有部署，有总结，有记录。");
            criteria3.setWeight(5);
            criteria3.setCategory("能");
            criteria3.setDisplayOrder(3);
            
            EvaluationCriteria criteria4 = new EvaluationCriteria();
            criteria4.setCode("N002");
            criteria4.setName("指导工作");
            criteria4.setDescription("熟悉教育理论和教学模式，对教育教学工作有新创新，目标植树明确具体，对教师教育教学工作能做出正确分析和准确评价。");
            criteria4.setWeight(5);
            criteria4.setCategory("能");
            criteria4.setDisplayOrder(4);
            
            EvaluationCriteria criteria5 = new EvaluationCriteria();
            criteria5.setCode("N003");
            criteria5.setName("组织协调与动态处理");
            criteria5.setDescription("有较强的角色意识，善于综合协调和深入基层工作，善于协调各方部门关系，统一目标，执行力强，积极分解部门工作任务思路，建议，推进各项工作有效落实。");
            criteria5.setWeight(10);
            criteria5.setCategory("能");
            criteria5.setDisplayOrder(5);
            
            // 勤 (15分)
            EvaluationCriteria criteria6 = new EvaluationCriteria();
            criteria6.setCode("Q001");
            criteria6.setName("精神状态与工作投入");
            criteria6.setDescription("始终保持积极向上的精神状态，全身心投入本职工作，身体力行，工作踏踏实实，勤勤恳恳，任劳任怨。");
            criteria6.setWeight(15);
            criteria6.setCategory("勤");
            criteria6.setDisplayOrder(6);
            
            // 绩 (40分)
            EvaluationCriteria criteria7 = new EvaluationCriteria();
            criteria7.setCode("J001");
            criteria7.setName("任务完成");
            criteria7.setDescription("完成领导交办的中心任务，积极主动开展工作，保质保量地完成各项分管工作的目标任务。");
            criteria7.setWeight(20);
            criteria7.setCategory("绩");
            criteria7.setDisplayOrder(7);
            
            EvaluationCriteria criteria8 = new EvaluationCriteria();
            criteria8.setCode("J002");
            criteria8.setName("工作业绩");
            criteria8.setDescription("分管工作亮点突出，效果明显，得到学校教师两级的好评。");
            criteria8.setWeight(20);
            criteria8.setCategory("绩");
            criteria8.setDisplayOrder(8);
            
            // 廉 (10分)
            EvaluationCriteria criteria9 = new EvaluationCriteria();
            criteria9.setCode("L001");
            criteria9.setName("廉洁自律");
            criteria9.setDescription("严格执行廉洁自律有关规定，按规定程序办事，没有利用职权谋取利益和收受好处，自觉接受群众监督。");
            criteria9.setWeight(10);
            criteria9.setCategory("廉");
            criteria9.setDisplayOrder(9);
            
            // 保存所有评价指标
            criteriaRepository.save(criteria1);
            criteriaRepository.save(criteria2);
            criteriaRepository.save(criteria3);
            criteriaRepository.save(criteria4);
            criteriaRepository.save(criteria5);
            criteriaRepository.save(criteria6);
            criteriaRepository.save(criteria7);
            criteriaRepository.save(criteria8);
            criteriaRepository.save(criteria9);
            
            // 初始化测评数据
            initEvaluationData();
        }
    }

    @Override
    public EvaluationCriteria getCriteriaById(Long id) {
        if (id == null) {
            return null;
        }
        return criteriaRepository.findById(id).orElse(null);
    }
    
    @Override
    public EvaluationPeriod getCurrentPeriod() {
        Date now = new Date();
        List<EvaluationPeriod> currentPeriods = periodRepository.findCurrentActivePeriods(now);
        
        if (currentPeriods.isEmpty()) {
            return null;
        }
        
        // 如果有多个有效周期，返回最新创建的
        return currentPeriods.get(0);
    }
    
    @Override
    @Transactional
    public boolean createPeriod(EvaluationPeriod period) {
        if (period == null || period.getName() == null || period.getStartTime() == null || period.getEndTime() == null) {
            return false;
        }
        
        // 验证开始时间在结束时间之前
        if (period.getStartTime().after(period.getEndTime())) {
            return false;
        }
        
        periodRepository.save(period);
        return true;
    }
    
    @Override
    @Transactional
    public boolean updatePeriod(EvaluationPeriod period) {
        if (period == null || period.getId() == null) {
            return false;
        }
        
        Optional<EvaluationPeriod> existingPeriod = periodRepository.findById(period.getId());
        if (existingPeriod.isEmpty()) {
            return false;
        }
        
        // 验证开始时间在结束时间之前
        if (period.getStartTime() != null && period.getEndTime() != null 
            && period.getStartTime().after(period.getEndTime())) {
            return false;
        }
        
        periodRepository.save(period);
        return true;
    }
    
    /**
     * 根据ID查找测评周期
     * @param id 测评周期ID
     * @return 测评周期Optional对象
     */
    @Override
    public Optional<EvaluationPeriod> findPeriodById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return periodRepository.findById(id);
    }
    
    /**
     * 获取所有测评周期
     * @return 测评周期列表
     */
    @Override
    public List<EvaluationPeriod> listAllPeriods() {
        return periodRepository.findAll();
    }
    
    /**
     * 分页获取测评周期
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<EvaluationPeriod> listPeriodsByPage(int pageNum, int pageSize) {
        // 确保页码从1开始
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        
        // 创建分页请求（注意：Spring Data JPA的页码从0开始）
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "startTime"));
        
        // 执行分页查询
        Page<EvaluationPeriod> page = periodRepository.findAll(pageable);
        
        // 构建分页结果
        return new PageResult<>(
            pageNum,
            pageSize,
            page.getTotalElements(),
            (int) page.getTotalPages(),
            page.getContent()
        );
    }
    
    @Override
    public boolean hasUserEvaluatedCadreInCurrentPeriod(Long cadreId, Long userId) {
        if (cadreId == null || userId == null) {
            return false;
        }
        
        EvaluationPeriod currentPeriod = getCurrentPeriod();
        if (currentPeriod == null) {
            return false;
        }
        
        // 检查用户在当前周期是否已经评价过该干部
        Integer count = evaluationRepository.countByCadreIdAndUserIdAndPeriodId(cadreId, userId, currentPeriod.getId());
        return count != null && count > 0;
    }
    
    /**
     * 初始化测评周期
     */
    @Transactional
    public void initEvaluationPeriod() {
        if (periodRepository.count() == 0) {
            // 获取当前时间
            Date now = new Date();
            
            // 添加当前活跃的测评周期
            EvaluationPeriod currentPeriod = new EvaluationPeriod();
            currentPeriod.setName("2024年第二季度中层干部测评");
            currentPeriod.setStartTime(now);
            // 结束时间为30天后
            Date endTime = new Date(now.getTime() + 30L * 24 * 60 * 60 * 1000);
            currentPeriod.setEndTime(endTime);
            currentPeriod.setIsActive(true);
            currentPeriod.setDescription("当前测评周期，用于对中层干部进行全面评估，每位用户只能对每位干部评价一次");
            periodRepository.save(currentPeriod);
            
            // 添加已结束的测评周期
            EvaluationPeriod pastPeriod = new EvaluationPeriod();
            pastPeriod.setName("2024年第一季度中层干部测评");
            // 开始时间为90天前
            Date pastStart = new Date(now.getTime() - 90L * 24 * 60 * 60 * 1000);
            pastPeriod.setStartTime(pastStart);
            // 结束时间为30天前
            Date pastEnd = new Date(now.getTime() - 30L * 24 * 60 * 60 * 1000);
            pastPeriod.setEndTime(pastEnd);
            pastPeriod.setIsActive(false);
            pastPeriod.setDescription("已结束的测评周期，用于存档历史测评数据");
            periodRepository.save(pastPeriod);
            
            // 添加未来的测评周期
            EvaluationPeriod futurePeriod = new EvaluationPeriod();
            futurePeriod.setName("2024年第三季度中层干部测评");
            // 开始时间为60天后
            Date futureStart = new Date(now.getTime() + 60L * 24 * 60 * 60 * 1000);
            futurePeriod.setStartTime(futureStart);
            // 结束时间为90天后
            Date futureEnd = new Date(now.getTime() + 90L * 24 * 60 * 60 * 1000);
            futurePeriod.setEndTime(futureEnd);
            futurePeriod.setIsActive(true);
            futurePeriod.setDescription("即将到来的测评周期，系统将在该周期开始时自动激活");
            periodRepository.save(futurePeriod);
        }
    }
} 