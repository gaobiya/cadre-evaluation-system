package com.example.demo.controller;

import com.example.demo.entity.PageResult;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Object> login(@RequestBody(required = false) User userParam, HttpSession session) {
        try {
            if (userParam == null || userParam.getUsername() == null || userParam.getPassword() == null) {
                return Result.fail("用户名和密码不能为空");
            }
            
            // 尝试从数据库验证
            User loginUser = userService.login(userParam.getUsername(), userParam.getPassword());
            
            if (loginUser != null) {
                // 登录成功，记录用户信息
                // 将密码设为null，避免返回给前端
                loginUser.setPassword(null);
                
                // 保存登录状态到Session
                session.setAttribute("loginUser", loginUser);
                
                // 创建简化版用户信息返回
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", loginUser.getId());
                userInfo.put("username", loginUser.getUsername());
                userInfo.put("name", loginUser.getName());
                userInfo.put("department", loginUser.getDepartment());
                userInfo.put("role", loginUser.getRole());
                
                return Result.success("登录成功", userInfo);
            } else {
                return Result.fail("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.fail("系统错误: " + e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody User user) {
        boolean result = userService.register(user);
        return result ? Result.success(true) : Result.fail("注册失败，用户名已存在");
    }
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            return Result.success(loginUser);
        }
        return Result.fail("未登录");
    }
    
    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<Boolean> logout(HttpSession session) {
        session.removeAttribute("loginUser");
        return Result.success(true);
    }
    
    /**
     * 获取所有用户
     */
    @GetMapping("/list")
    public Result<List<User>> listAll(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return Result.fail("无权限");
        }
        return Result.success(userService.listAll());
    }
    
    /**
     * 分页获取用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<User>> listByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return Result.fail("无权限");
        }
        return Result.success(userService.listByPage(pageNum, pageSize));
    }
} 