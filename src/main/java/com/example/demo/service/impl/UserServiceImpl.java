package com.example.demo.service.impl;

import com.example.demo.entity.PageResult;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return null;
        }
        
        User user = userOpt.get();
        
        if (password.equals(user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }
    
    @Override
    @Transactional
    public boolean register(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return false;
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;
        }
        
        // 设置默认值
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        // 保存用户
        userRepository.save(user);
        
        return true;
    }
    
    @Override
    public List<User> listAll() {
        return userRepository.findAll();
    }
    
    @Override
    public PageResult<User> listByPage(int pageNum, int pageSize) {
        // 确保页码从1开始
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        
        // 创建分页请求（注意：Spring Data JPA的页码从0开始）
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        
        // 执行分页查询
        Page<User> page = userRepository.findAll(pageable);
        
        // 构建分页结果
        return new PageResult<>(
            pageNum,
            pageSize,
            page.getTotalElements(),
            page.getTotalPages(),
            page.getContent()
        );
    }
    
    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public boolean update(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        
        // 检查用户是否存在
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (!existingUserOpt.isPresent()) {
            return false;
        }
        
        User existingUser = existingUserOpt.get();
        
        // 检查是否修改了用户名，如果修改了需要检查是否与其他用户冲突
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                return false;
            }
        }
        
        // 更新字段，保留原始数据
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getDepartment() != null) {
            existingUser.setDepartment(user.getDepartment());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        
        existingUser.setUpdateTime(new Date());
        
        // 保存更新
        userRepository.save(existingUser);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            return false;
        }
        
        userRepository.deleteById(id);
        
        return true;
    }
    
    /**
     * 初始化系统管理员账号
     */
    @Transactional
    public void initAdminUser() {
        // 检查是否已存在管理员账号
        boolean adminExists = userRepository.existsByRole("ADMIN");
        
        if (!adminExists) {
            // 创建管理员用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setName("系统管理员");
            admin.setDepartment("系统管理部门");
            admin.setRole("ADMIN");
            admin.setCreateTime(new Date());
            admin.setUpdateTime(new Date());
            
            // 保存管理员用户
            userRepository.save(admin);
            
            // 添加测试用户 - 各部门员工
            String[] departments = {"人力资源部", "财务部", "技术部", "销售部", "市场部", "产品部", "研发部", "行政部"};
            for (int i = 1; i <= 8; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setPassword("password" + i);
                user.setName("普通用户" + i);
                user.setDepartment(departments[i-1]);
                user.setRole("USER");
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                
                userRepository.save(user);
            }
            
            // 添加部门经理用户
            for (int i = 1; i <= 5; i++) {
                User manager = new User();
                manager.setUsername("manager" + i);
                manager.setPassword("manager" + i);
                manager.setName("部门经理" + i);
                manager.setDepartment(departments[i-1]);
                manager.setRole("USER");
                manager.setCreateTime(new Date());
                manager.setUpdateTime(new Date());
                
                userRepository.save(manager);
            }
        }
    }
    
    /**
     * 初始化用户数据
     * 供DataInitializer调用
     */
    @Transactional
    public void initUsers() {
        initAdminUser();
    }
} 