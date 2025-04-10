package com.example.demo.controller;

import com.example.demo.entity.Cadre;
import com.example.demo.entity.Evaluation;
import com.example.demo.entity.EvaluationCriteria;
import com.example.demo.entity.User;
import com.example.demo.service.CadreService;
import com.example.demo.service.EvaluationService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测评控制器
 */
@Controller
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private CadreService cadreService;

    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private UserService userService;

    /**
     * 显示干部选择页面
     */
    @GetMapping("/select-cadre")
    public String selectCadre(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 获取所有干部列表
        List<Cadre> cadreList = cadreService.listAll();
        model.addAttribute("cadreList", cadreList);
        
        return "evaluation/select-cadre";
    }

    /**
     * 显示测评表单
     */
    @GetMapping("/form")
    public String showEvaluationForm(@RequestParam(required = true) Long cadreId, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Cadre cadre = cadreService.getById(cadreId);
        if (cadre == null) {
            return "redirect:/evaluation/select-cadre";
        }

        List<EvaluationCriteria> criteriaList = evaluationService.listAllCriteria();
        
        model.addAttribute("cadre", cadre);
        model.addAttribute("criteriaList", criteriaList);
        model.addAttribute("evaluator", loginUser);
        
        return "evaluation/form";
    }

    /**
     * 提交测评
     */
    @PostMapping("/submit")
    @ResponseBody
    public Map<String, Object> submitEvaluation(@RequestBody List<Map<String, Object>> evaluationData, 
                                               HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Map.of("success", false, "message", "请先登录");
        }

        if (evaluationData == null || evaluationData.isEmpty()) {
            return Map.of("success", false, "message", "评价数据不能为空");
        }

        // 获取干部ID和评语
        Long cadreId = null;
        String comments = null;
        try {
            cadreId = Long.parseLong(evaluationData.get(0).get("cadreId").toString());
            comments = (String) evaluationData.get(0).get("comments");
        } catch (Exception e) {
            return Map.of("success", false, "message", "干部ID无效");
        }

        // 检查干部是否存在
        Cadre cadre = cadreService.getById(cadreId);
        if (cadre == null) {
            return Map.of("success", false, "message", "干部不存在");
        }

        // 删除该用户之前对该干部的评价
        evaluationService.deleteEvaluationByEvaluator(cadreId, loginUser.getUsername(), loginUser.getDepartment());

        // 构建评价数据
        List<Evaluation> evaluations = new ArrayList<>();
        for (Map<String, Object> data : evaluationData) {
            try {
                Long criteriaId = Long.parseLong(data.get("criteriaId").toString());
                Integer score = Integer.parseInt(data.get("score").toString());

                Evaluation evaluation = new Evaluation();
                evaluation.setCadreId(cadreId);
                evaluation.setCriteriaId(criteriaId);
                evaluation.setScore(score);
                // 只在第一个指标记录中设置评语
                if (criteriaId.equals(evaluationData.get(0).get("criteriaId"))) {
                    evaluation.setComments(comments);
                }
                evaluation.setEvaluatorName(loginUser.getUsername());
                evaluation.setEvaluatorDept(loginUser.getDepartment());
                evaluation.setEvaluationTime(new Date());

                evaluations.add(evaluation);
            } catch (Exception e) {
                return Map.of("success", false, "message", "评价数据格式不正确");
            }
        }

        // 批量保存评价
        boolean success = evaluationService.batchAddEvaluations(evaluations);
        if (success) {
            return Map.of("success", true, "message", "评价提交成功");
        } else {
            return Map.of("success", false, "message", "评价提交失败");
        }
    }

    /**
     * 显示测评结果页面
     */
    @GetMapping("/result")
    public String showResult(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        
        // 获取所有干部
        List<Cadre> cadreList = cadreService.listAll();
        List<Map<String, Object>> resultList = cadreList.stream().map(cadre -> {
            Long cadreId = cadre.getId();
            
            // 获取评价人数
            int evaluatorCount = cadreService.getEvaluatorCount(cadreId);
            
            // 获取各项指标的平均分
            double abilityScore = cadreService.getAverageScore(cadreId, "工作能力");
            double attitudeScore = cadreService.getAverageScore(cadreId, "工作态度");
            double performanceScore = cadreService.getAverageScore(cadreId, "工作业绩");
            double disciplineScore = cadreService.getAverageScore(cadreId, "工作纪律");
            
            // 获取总平均分
            double averageScore = cadreService.getAverageScore(cadreId);
            
            return Map.of(
                "cadre", cadre,
                "evaluatorCount", evaluatorCount,
                "abilityScore", String.format("%.2f", abilityScore),
                "attitudeScore", String.format("%.2f", attitudeScore),
                "performanceScore", String.format("%.2f", performanceScore),
                "disciplineScore", String.format("%.2f", disciplineScore),
                "averageScore", String.format("%.2f", averageScore)
            );
        }).collect(Collectors.toList());
        
        model.addAttribute("resultList", resultList);
        
        return "evaluation/result";
    }
} 