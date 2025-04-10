package com.example.demo.interceptor;

import com.example.demo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的URI
        String uri = request.getRequestURI();
        
        // 静态资源和公开接口不需要登录就可以访问
        if (uri.startsWith("/api/user/login") || uri.startsWith("/api/user/register") || 
            uri.startsWith("/login") || uri.startsWith("/register") || 
            uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/")) {
            return true;
        }
        
        // API接口的登录检查，返回JSON数据
        if (uri.startsWith("/api/")) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("loginUser");
            if (user == null) {
                // 未登录时返回401状态码
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"未登录\",\"data\":null}");
                return false;
            }
            return true;
        }
        
        // 页面的登录检查，重定向到登录页面
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }
        
        return true;
    }
} 