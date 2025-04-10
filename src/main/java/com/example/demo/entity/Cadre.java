package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 中层干部实体类
 */
@Entity
@Table(name = "cadre")
public class Cadre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;        // 姓名
    private String department;  // 部门
    private String position;    // 职位
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;    // 创建时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;    // 更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
} 