package com.example.demo.config;

import com.example.demo.repository.CadreRepository;
import com.example.demo.repository.EvaluationCriteriaRepository;
import com.example.demo.repository.EvaluationPeriodRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CadreService;
import com.example.demo.service.EvaluationService;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.CadreServiceImpl;
import com.example.demo.service.impl.EvaluationServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据初始化器
 * 用于在应用启动时初始化数据库中的基础数据
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CadreService cadreService;
    
    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CadreRepository cadreRepository;
    
    @Autowired
    private EvaluationCriteriaRepository criteriaRepository;
    
    @Autowired
    private EvaluationPeriodRepository periodRepository;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("===== 检查系统数据 =====");
        
        try {
            // 1. 首先初始化用户数据 - 因为评价数据需要用户ID
            long userCount = userRepository.count();
            if (userCount == 0) {
                logger.info("用户数据不存在，正在初始化用户数据...");
                ((UserServiceImpl) userService).initUsers();
            } else {
                logger.info("用户数据已存在，跳过初始化 (共 {} 条记录)", userCount);
            }
            
            // 2. 初始化测评周期 - 因为评价数据需要周期ID
            long periodCount = periodRepository.count();
            if (periodCount == 0) {
                logger.info("测评周期数据不存在，正在初始化测评周期数据...");
                ((EvaluationServiceImpl) evaluationService).initEvaluationPeriod();
            } else {
                logger.info("测评周期数据已存在，跳过初始化 (共 {} 条记录)", periodCount);
            }
            
            // 3. 初始化干部数据 - 评价数据需要干部ID
            long cadreCount = cadreRepository.count();
            if (cadreCount == 0) {
                logger.info("干部数据不存在，正在初始化干部数据...");
                ((CadreServiceImpl) cadreService).initCadres();
            } else {
                logger.info("干部数据已存在，跳过初始化 (共 {} 条记录)", cadreCount);
            }
            
            // 4. 初始化评价指标数据和评价数据
            long criteriaCount = criteriaRepository.count();
            if (criteriaCount == 0) {
                logger.info("评价指标数据不存在，正在初始化评价指标数据和评价数据...");
                ((EvaluationServiceImpl) evaluationService).initEvaluationCriteria();
                // 注意：initEvaluationCriteria方法会在内部调用initEvaluationData方法
            } else {
                logger.info("评价指标数据已存在，跳过初始化 (共 {} 条记录)", criteriaCount);
            }
            
            logger.info("系统数据检查完成！");
        } catch (Exception e) {
            logger.error("数据初始化过程中发生错误: {}", e.getMessage(), e);
            throw e; // 重新抛出异常，让Spring Boot知道初始化失败
        }
        
        // 输出系统信息
        logger.info("===== 系统启动完成 =====");
    }
} 