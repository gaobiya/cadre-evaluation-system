package com.example.demo.service;

import com.example.demo.entity.PageResult;
import com.example.demo.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息，如果登录失败返回null
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(User user);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> listAll();
    
    /**
     * 分页获取用户列表
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<User> listByPage(int pageNum, int pageSize);
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(Long id);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    boolean update(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean delete(Long id);
} 