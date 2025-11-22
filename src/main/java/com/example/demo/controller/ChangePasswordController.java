package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/change-password")
public class ChangePasswordController {

    @Autowired
    private UserService userService;

    // 显示修改密码页面
    @GetMapping
    public String showChangePasswordForm() {
        return "change-password";
    }

    // 处理修改密码请求
    @PostMapping
    public String processChangePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       HttpSession session,
                                       Model model) {
        
        // 获取当前登录用户的用户名
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        // 验证新密码和确认密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "新密码和确认密码不一致");
            return "change-password";
        }

        // 验证新密码长度
        if (newPassword.length() < 6) {
            model.addAttribute("error", "新密码长度至少为6位");
            return "change-password";
        }

        // 调用服务层修改密码
        boolean success = userService.changePassword(username, oldPassword, newPassword);
        
        if (success) {
            model.addAttribute("message", "密码修改成功");
        } else {
            model.addAttribute("error", "原密码错误");
        }
        
        return "change-password";
    }
}