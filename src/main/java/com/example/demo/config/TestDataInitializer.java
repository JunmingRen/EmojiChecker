package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 测试数据初始化器 - 在应用启动时自动创建测试用户
 */
@Component
public class TestDataInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("初始化测试用户数据...");
        userService.initTestUsers();
        System.out.println("测试用户数据初始化完成！");
    }
}