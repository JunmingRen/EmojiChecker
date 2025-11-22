package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户控制器 - 处理用户注册相关功能
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * 处理用户注册请求
     */
    @PostMapping("/register")
    public String register(@RequestParam String username, 
                          @RequestParam String password, 
                          @RequestParam String confirmPassword,
                          @RequestParam String role,
                          @RequestParam String name,
                          Model model) {
        try {
            // 验证密码
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "两次输入的密码不一致");
                return "register";
            }

            // 调用服务层注册用户
            userService.createTestUser(username, password, role, name);
            
            System.out.println("创建用户成功: " + username);

            model.addAttribute("success", "注册成功，请登录");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "注册失败: " + e.getMessage());
            return "register";
        }
    }
}