package com.example.demo.config;

import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/cadre/**", "/evaluation/**")
                .excludePathPatterns("/login", "/register", "/", "/api/user/login", "/api/user/register");
        
        // 管理员权限拦截器
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/cadre/list", "/api/cadre/add", "/api/cadre/update", "/api/cadre/delete/**")
                .excludePathPatterns("/login", "/register", "/api/user/login", "/api/user/register");
    }
    
    /**
     * 登录拦截器
     */
    public class LoginInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");
            
            if (loginUser == null) {
                logger.warn("未登录用户尝试访问受保护资源: {}", request.getRequestURI());
                
                // API请求返回JSON
                if (request.getRequestURI().startsWith("/api/")) {
                    sendJsonResponse(response, Result.fail(401, "请先登录"));
                    return false;
                }
                
                // 页面请求重定向到登录页
                response.sendRedirect("/login");
                return false;
            }
            
            return true;
        }
    }
    
    /**
     * 管理员权限拦截器
     */
    public class AdminInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");
            
            // 检查是否登录且角色为管理员
            if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
                logger.warn("非管理员用户尝试访问管理功能: {}, 用户: {}", 
                    request.getRequestURI(), 
                    loginUser != null ? loginUser.getUsername() : "未登录");
                
                // API请求返回JSON
                if (request.getRequestURI().startsWith("/api/")) {
                    sendJsonResponse(response, Result.fail(403, "权限不足，需要管理员权限"));
                    return false;
                }
                
                // 页面请求重定向到首页
                response.sendRedirect("/");
                return false;
            }
            
            return true;
        }
    }
    
    /**
     * 发送JSON响应
     */
    private void sendJsonResponse(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
        writer.close();
    }
} 