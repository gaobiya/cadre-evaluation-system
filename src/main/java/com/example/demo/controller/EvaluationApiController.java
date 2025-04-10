package com.example.demo.controller;

import com.example.demo.entity.Evaluation;
import com.example.demo.entity.EvaluationCriteria;
import com.example.demo.entity.EvaluationPeriod;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.entity.PageResult;
import com.example.demo.service.EvaluationService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测评API控制器
 */
@RestController
@RequestMapping("/api/evaluation")
public class EvaluationApiController {

    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private UserService userService;

    /**
     * 获取所有评价指标
     */
    @GetMapping("/criteria/list")
    public Result<List<EvaluationCriteria>> listAllCriteria() {
        try {
            List<EvaluationCriteria> criteriaList = evaluationService.listAllCriteria();
            return Result.success(criteriaList);
        } catch (Exception e) {
            return Result.fail("获取评价指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取干部的评价列表
     */
    @GetMapping("/list/{cadreId}")
    public Result<List<Evaluation>> listByCadreId(@PathVariable Long cadreId) {
        if (cadreId == null) {
            return Result.fail("干部ID不能为空");
        }
        
        try {
            List<Evaluation> evaluations = evaluationService.listByCadreId(cadreId);
            return Result.success(evaluations);
        } catch (Exception e) {
            return Result.fail("获取评价列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户对指定干部的评价
     */
    @GetMapping("/list/my/{cadreId}")
    public Result<List<Evaluation>> getMyEvaluations(@PathVariable Long cadreId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("请先登录");
        }
        
        if (cadreId == null) {
            return Result.fail("干部ID不能为空");
        }
        
        try {
            List<Evaluation> evaluations = evaluationService.listByEvaluator(
                cadreId, loginUser.getUsername(), loginUser.getDepartment());
            return Result.success(evaluations);
        } catch (Exception e) {
            return Result.fail("获取评价列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个测评周期详细信息
     */
    @GetMapping("/period/{id}")
    public Result<EvaluationPeriod> getPeriodById(@PathVariable Long id) {
        try {
            if (id == null) {
                return Result.fail("测评周期ID不能为空");
            }
            
            Optional<EvaluationPeriod> periodOpt = evaluationService.findPeriodById(id);
            if (periodOpt.isEmpty()) {
                return Result.fail("找不到指定的测评周期");
            }
            
            return Result.success(periodOpt.get());
        } catch (Exception e) {
            return Result.fail("获取测评周期失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加评价
     */
    @PostMapping("/batch/add")
    public Result<Boolean> batchAddEvaluations(@RequestBody List<Evaluation> evaluations, HttpSession session) {
        if (evaluations == null || evaluations.isEmpty()) {
            return Result.fail("测评数据不能为空");
        }
        
        // 检查用户是否登录
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("您尚未登录，请先登录");
        }
        
        try {
            // 检查当前是否在测评周期内
            EvaluationPeriod currentPeriod = evaluationService.getCurrentPeriod();
            if (currentPeriod == null) {
                return Result.fail("当前不在测评周期内，无法进行测评");
            }
            
            // 设置用户ID和评价时间
            Long cadreId = evaluations.get(0).getCadreId();
            if (cadreId == null) {
                return Result.fail("缺少被评价干部信息");
            }
            
            // 检查用户在当前周期是否已经评价过该干部
            if (evaluationService.hasUserEvaluatedCadreInCurrentPeriod(cadreId, loginUser.getId())) {
                return Result.fail("您在本次测评周期内已经对该干部进行过评价，不能重复评价");
            }
            
            Date now = new Date();
            for (Evaluation evaluation : evaluations) {
                evaluation.setEvaluationTime(now);
                evaluation.setUserId(loginUser.getId());
            }
            
            boolean success = evaluationService.batchAddEvaluations(evaluations);
            if (success) {
                return Result.success("测评提交成功", true);
            } else {
                return Result.fail("测评提交失败，请稍后再试");
            }
        } catch (Exception e) {
            return Result.fail("提交测评失败：" + e.getMessage());
        }
    }

    /**
     * 获取评价指标详情
     */
    @GetMapping("/criteria/{id}")
    public Result<EvaluationCriteria> getCriteriaById(@PathVariable Long id) {
        try {
            // 查找评价标准
            EvaluationCriteria criteria = evaluationService.getCriteriaById(id);
            if (criteria == null) {
                return Result.fail("找不到指定的评价指标");
            }
            return Result.success(criteria);
        } catch (Exception e) {
            return Result.fail("获取评价指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前测评周期
     */
    @GetMapping("/period/current")
    public Result<EvaluationPeriod> getCurrentPeriod() {
        try {
            EvaluationPeriod period = evaluationService.getCurrentPeriod();
            if (period != null) {
                return Result.success(period);
            } else {
                return Result.fail("当前没有有效的测评周期");
            }
        } catch (Exception e) {
            return Result.fail("获取测评周期失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有测评周期
     */
    @GetMapping("/period/list")
    public Result<List<EvaluationPeriod>> listAllPeriods() {
        try {
            List<EvaluationPeriod> periods = evaluationService.listAllPeriods();
            return Result.success(periods);
        } catch (Exception e) {
            return Result.fail("获取测评周期失败：" + e.getMessage());
        }
    }

    /**
     * 分页获取测评周期
     */
    @GetMapping("/period/page")
    public Result<PageResult<EvaluationPeriod>> listPeriodsByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageResult<EvaluationPeriod> pageResult = evaluationService.listPeriodsByPage(pageNum, pageSize);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.fail("分页获取测评周期失败：" + e.getMessage());
        }
    }

    /**
     * 创建测评周期
     */
    @PostMapping("/period/create")
    public Result<Boolean> createPeriod(@RequestBody EvaluationPeriod period, HttpSession session) {
        // 检查用户是否是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return Result.fail("只有管理员才能创建测评周期");
        }
        
        try {
            boolean success = evaluationService.createPeriod(period);
            if (success) {
                return Result.success("测评周期创建成功", true);
            } else {
                return Result.fail("测评周期创建失败，请检查输入参数");
            }
        } catch (Exception e) {
            return Result.fail("创建测评周期失败：" + e.getMessage());
        }
    }

    /**
     * 更新测评周期
     */
    @PostMapping("/period/update")
    public Result<Boolean> updatePeriod(@RequestBody EvaluationPeriod period, HttpSession session) {
        // 检查用户是否是管理员
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return Result.fail("只有管理员才能更新测评周期");
        }
        
        try {
            boolean success = evaluationService.updatePeriod(period);
            if (success) {
                return Result.success("测评周期更新成功", true);
            } else {
                return Result.fail("测评周期更新失败，请检查输入参数");
            }
        } catch (Exception e) {
            return Result.fail("更新测评周期失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否可以对特定干部进行评价
     */
    @GetMapping("/check-permission")
    public Result<Boolean> checkEvaluationPermission(@RequestParam Long cadreId, HttpSession session) {
        // 检查用户是否登录
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("您尚未登录，请先登录");
        }
        
        try {
            // 检查当前是否在测评周期内
            EvaluationPeriod currentPeriod = evaluationService.getCurrentPeriod();
            if (currentPeriod == null) {
                return Result.fail("当前不在测评周期内，无法进行测评");
            }
            
            // 检查用户在当前周期是否已经评价过该干部
            boolean hasEvaluated = evaluationService.hasUserEvaluatedCadreInCurrentPeriod(cadreId, loginUser.getId());
            if (hasEvaluated) {
                return Result.fail("您在本次测评周期内已经对该干部进行过评价，不能重复评价");
            }
            
            return Result.success("可以进行评价", true);
        } catch (Exception e) {
            return Result.fail("检查评价权限失败：" + e.getMessage());
        }
    }
} 