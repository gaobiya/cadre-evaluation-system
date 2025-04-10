package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

/**
 * 测评记录实体类
 */
@Entity
@Table(name = "evaluation")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cadre_id", nullable = false)
    private Long cadreId;          // 干部ID
    
    @Column(name = "criteria_id", nullable = false)
    private Long criteriaId;       // 测评指标ID
    
    @Column(nullable = false)
    private Integer score;         // 得分
    
    @Column(name = "evaluator_name", length = 50)
    private String evaluatorName;  // 评价人姓名
    
    @Column(name = "evaluator_dept", length = 100)
    private String evaluatorDept;  // 评价人部门
    
    @Column(name = "user_id")
    private Long userId;           // 用户ID，用于识别唯一用户
    
    @Column(name = "period_id")
    private Long periodId;         // 评价周期ID
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "evaluation_time")
    private Date evaluationTime;   // 评价时间
    
    @Column(length = 500)
    private String comments;       // 评语

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCadreId() {
        return cadreId;
    }

    public void setCadreId(Long cadreId) {
        this.cadreId = cadreId;
    }

    public Long getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

    public String getEvaluatorDept() {
        return evaluatorDept;
    }

    public void setEvaluatorDept(String evaluatorDept) {
        this.evaluatorDept = evaluatorDept;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public Date getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(Date evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
} 