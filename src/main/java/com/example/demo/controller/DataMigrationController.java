package com.example.demo.controller;

import com.example.demo.service.DataMigrationService;
import com.example.demo.util.CourseDataInitializer;
import com.example.demo.util.StudentCourseDataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 数据迁移控制器 - 提供API端点来触发和监控数据迁移过程
 */
@RestController
@RequestMapping("/api/migration")
public class DataMigrationController {

    @Autowired
    private CourseDataInitializer courseDataInitializer;
    
    @Autowired
    private StudentCourseDataInitializer studentCourseDataInitializer;

    @Autowired
    private DataMigrationService dataMigrationService;

    /**
     * 触发数据迁移操作
     * 将users表中的教师和学生数据分别迁移到teachers和students表
     */
    @PostMapping("/start")
    public ResponseEntity<?> startMigration() {
        try {
            DataMigrationService.MigrationResult result = dataMigrationService.migrateUsersToEntities();
            return ResponseEntity.ok("数据迁移完成。\n" +
                    "成功迁移 " + result.getTeacherCount() + " 个教师\n" +
                    "成功迁移 " + result.getStudentCount() + " 个学生\n" +
                    "管理员用户 " + result.getAdminCount() + " 个\n" +
                    "出错用户 " + result.getErrors().size() + " 个");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("数据迁移失败: " + e.getMessage());
        }
    }

    /**
     * 验证数据迁移结果
     * 检查是否所有的教师和学生数据都已成功迁移
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateMigration() {
        try {
            DataMigrationService.MigrationValidationResult validationResult = dataMigrationService.validateMigration();
            
            if (validationResult.isSuccess()) {
                return ResponseEntity.ok("数据迁移验证成功！所有用户数据已正确迁移。");
            } else {
                StringBuilder message = new StringBuilder("数据迁移验证发现问题：\n");
                if (!validationResult.getMissingTeachers().isEmpty()) {
                    message.append("缺失教师记录: ")
                           .append(String.join(", ", validationResult.getMissingTeachers()))
                           .append("\n");
                }
                if (!validationResult.getMissingStudents().isEmpty()) {
                    message.append("缺失学生记录: ")
                           .append(String.join(", ", validationResult.getMissingStudents()));
                }
                return ResponseEntity.badRequest().body(message.toString());
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("验证失败: " + e.getMessage());
        }
    }

    /**
     * 获取迁移状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<?> getMigrationStatus() {
        try {
            DataMigrationService.MigrationValidationResult validationResult = dataMigrationService.validateMigration();
            
            if (validationResult.isSuccess()) {
                return ResponseEntity.ok("数据迁移已完成且验证通过。");
            } else {
                return ResponseEntity.ok("数据迁移未完成或存在问题。");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 初始化课程数据和学生课程关联
     * @return 操作结果
     */
    @GetMapping("/initialize-courses")
    public ResponseEntity<?> initializeCourseData() {
        try {
            courseDataInitializer.initializeTeacher1Courses();
            studentCourseDataInitializer.initializeStudentCourses();
            return ResponseEntity.ok("课程数据和学生课程关联初始化成功！");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("数据初始化失败: " + e.getMessage());
        }
    }
}