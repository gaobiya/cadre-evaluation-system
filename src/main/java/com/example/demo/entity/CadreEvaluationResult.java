package com.example.demo.entity;

import jakarta.persistence.Column;

/**
 * 干部测评结果数据传输对象
 */
public class CadreEvaluationResult {
    private Long cadreId;          // 干部ID
    private String cadreName;      // 干部姓名
    private String department;     // 部门
    private String position;       // 职位
    private double totalScore;     // 总分
    private int evaluatorCount;    // 评价人数
    private String comments;       // 评语
    
    // 各维度得分
    private double deScore;        // 德
    private double nengScore;      // 能
    private double qinScore;       // 勤
    private double jiScore;        // 绩
    private double lianScore;      // 廉

    public Long getCadreId() {
        return cadreId;
    }

    public void setCadreId(Long cadreId) {
        this.cadreId = cadreId;
    }

    public String getCadreName() {
        return cadreName;
    }

    public void setCadreName(String cadreName) {
        this.cadreName = cadreName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getEvaluatorCount() {
        return evaluatorCount;
    }

    public void setEvaluatorCount(int evaluatorCount) {
        this.evaluatorCount = evaluatorCount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public double getDeScore() {
        return deScore;
    }

    public void setDeScore(double deScore) {
        this.deScore = deScore;
    }

    public double getNengScore() {
        return nengScore;
    }

    public void setNengScore(double nengScore) {
        this.nengScore = nengScore;
    }

    public double getQinScore() {
        return qinScore;
    }

    public void setQinScore(double qinScore) {
        this.qinScore = qinScore;
    }

    public double getJiScore() {
        return jiScore;
    }

    public void setJiScore(double jiScore) {
        this.jiScore = jiScore;
    }

    public double getLianScore() {
        return lianScore;
    }

    public void setLianScore(double lianScore) {
        this.lianScore = lianScore;
    }
} 