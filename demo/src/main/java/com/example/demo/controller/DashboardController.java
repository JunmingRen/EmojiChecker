package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 仪表盘控制器 - 处理各角色页面路由
 */
@Controller
public class DashboardController {
    
    /**
     * 显示登录页面
     */
    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }
    
    /**
     * 处理登录请求
     * 根据角色跳转到对应的仪表盘
     */
    @PostMapping("/login")
    public String login(@RequestParam String role) {
        // 根据选择的角色重定向到对应的仪表盘
        switch (role) {
            case "teacher":
                return "redirect:/teacher/dashboard";
            case "student":
                return "redirect:/student/dashboard";
            case "admin":
                return "redirect:/admin/dashboard";
            default:
                return "redirect:/";
        }
    }
    
    /**
     * 显示教师仪表盘
     */
    @GetMapping("/teacher/dashboard")
    public String showTeacherDashboard() {
        return "teacher/dashboard";
    }
    
    /**
     * 显示学生仪表盘
     */
    @GetMapping("/student/dashboard")
    public String showStudentDashboard() {
        return "student/dashboard";
    }
    
    /**
     * 显示管理员仪表盘
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {
        return "admin/dashboard";
    }
    
    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
    
    /**
     * 处理错误页面
     */
    @GetMapping("/error")
    public String handleError() {
        return "redirect:/";
    }
}