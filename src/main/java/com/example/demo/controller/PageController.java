package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

/**
 * 页面控制器
 */
@Controller
public class PageController {
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("user", loginUser);
            model.addAttribute("isAdmin", "ADMIN".equals(loginUser.getRole()));
        }
        return "index";
    }
    
    /**
     * 干部列表页面 - 仅管理员可访问
     */
    @GetMapping("/cadre/list")
    public String cadreList(HttpSession session) {
        // 检查是否管理员权限
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }
        
        return "cadre/list";
    }
    
    /**
     * 检查用户权限
     */
    private boolean checkUserPermission(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        return loginUser != null;
    }
    
    /**
     * 检查管理员权限
     */
    private boolean checkAdminPermission(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        return loginUser != null && "ADMIN".equals(loginUser.getRole());
    }
    
    /**
     * 测评周期管理页面
     */
    @GetMapping("/evaluation/period")
    public String evaluationPeriod(HttpSession session) {
        // 只有管理员才能访问测评周期管理页面
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }
        
        return "evaluation/period-management";
    }
} 