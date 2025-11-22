package com.example.demo.controller;

import com.example.demo.service.EmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 表情符号控制器 - 处理表情相关的功能
 */
@Controller
public class EmojiController {
    
    @Autowired
    private EmojiService emojiService;
    
    /**
     * 提交表情
     */
    @PostMapping("/api/emoji/submit")
    @ResponseBody
    public ResponseEntity<?> submitEmoji(
            @RequestParam String emoji,
            @RequestParam String studentName,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) Long courseId) {
        
        try {
            System.out.println("收到表情提交请求 - 表情: " + emoji + ", 学生: " + studentName);
            // 参数验证
            if (emoji == null || emoji.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "表情不能为空"));
            }
            if (studentName == null || studentName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "学生名称不能为空"));
            }
            
            // 根据是否提供courseId和studentId选择不同的保存方式
            if (courseId != null && studentId != null && !studentId.trim().isEmpty()) {
                // 新的按课程提交方式
                emojiService.saveEmojiMessage(emoji, studentName, studentId, courseId);
                System.out.println("表情保存成功 - 表情: " + emoji + ", 学生: " + studentName + ", 课程ID: " + courseId);
            } else {
                // 兼容旧版本的提交方式
                emojiService.saveEmojiMessage(emoji, studentName);
                System.out.println("表情保存成功 - 表情: " + emoji + ", 学生: " + studentName);
            }
            
            return ResponseEntity.ok().body(Map.of("status", "success", "message", "表情提交成功"));
        } catch (Exception e) {
            System.out.println("表情提交失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "表情提交失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取表情统计数据
     */
    @GetMapping("/api/emoji/stats")
    @ResponseBody
    public ResponseEntity<?> getEmojiStats(@RequestParam(required = false) Long courseId) {
        try {
            // 根据是否提供courseId选择不同的数据获取方式
            Map<String, Integer> stats;
            if (courseId != null) {
                // 根据课程ID获取统计数据
                stats = emojiService.getEmojiStatsByCourseId(courseId);
            } else {
                // 获取所有统计数据（兼容旧版本）
                stats = emojiService.getEmojiStats();
            }
            
            // 获取最近的10个表情记录
            List<Map<String, String>> recentEmojis = emojiService.getRecentEmojis(10);
            
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "stats", stats,
                    "recentEmojis", recentEmojis
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "获取统计数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有表情消息历史，用于聊天界面显示
     */
    @GetMapping("/api/emoji/messages")
    @ResponseBody
    public ResponseEntity<?> getEmojiMessages(@RequestParam(required = false) Long courseId) {
        try {
            System.out.println("收到获取表情消息请求，课程ID: " + courseId);
            // 根据是否提供courseId选择不同的数据获取方式
            List<Map<String, String>> messages;
            if (courseId != null) {
                // 根据课程ID获取消息
                messages = emojiService.getEmojiMessagesByCourseId(courseId);
            } else {
                // 获取所有消息（兼容旧版本）
                messages = emojiService.getAllEmojiMessages();
            }
            System.out.println("成功获取表情消息，共 " + messages.size() + " 条");
            
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "messages", messages
            ));
        } catch (Exception e) {
            System.out.println("获取表情消息失败: " + e.getMessage());
            e.printStackTrace();
            // 出错时返回空消息列表而不是错误，这样界面仍能正常显示
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "messages", new ArrayList<>(),
                    "warning", "获取历史消息时出错，显示空列表"
            ));
        }
    }
    
    /**
     * 管理员API：获取表情数据统计（支持筛选）
     */
    @GetMapping("/api/admin/emoji-stats")
    @ResponseBody
    public ResponseEntity<?> getAdminEmojiStats(
            @RequestParam(defaultValue = "all") String dateRange,
            @RequestParam(defaultValue = "all") String emojiType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 获取表情统计数据
            Map<String, Object> stats = emojiService.getAdminEmojiStats(dateRange, emojiType);
            
            // 获取表情列表（用于分页显示）
            List<Map<String, Object>> emojiList = emojiService.getEmojiListForAdmin(dateRange, emojiType);
            
            // 分页处理
            int total = emojiList.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Map<String, Object>> paginatedList = (start < total) ? emojiList.subList(start, end) : List.of();
            
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "totalStats", stats,
                    "emojiList", paginatedList,
                    "total", total,
                    "page", page,
                    "pageSize", pageSize
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error", 
                    "message", "获取表情统计失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 管理员API：导出表情数据为CSV
     */
    @GetMapping("/api/admin/export/csv")
    public ResponseEntity<?> exportEmojiDataToCsv(
            @RequestParam(defaultValue = "all") String dateRange,
            @RequestParam(defaultValue = "all") String emojiType) {
        try {
            // 获取CSV数据
            String csvData = emojiService.exportEmojiDataToCsv(dateRange, emojiType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "emoji-data-" + System.currentTimeMillis() + ".csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error", 
                    "message", "导出CSV失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 管理员API：导出表情数据为PDF
     */
    @GetMapping("/api/admin/export/pdf")
    public ResponseEntity<?> exportEmojiDataToPdf(
            @RequestParam(defaultValue = "all") String dateRange,
            @RequestParam(defaultValue = "all") String emojiType) {
        try {
            // 获取PDF数据
            byte[] pdfData = emojiService.exportEmojiDataToPdf(dateRange, emojiType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "emoji-data-" + System.currentTimeMillis() + ".pdf");
            headers.setContentLength(pdfData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error", 
                    "message", "导出PDF失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 学生表情聊天页面 - 根据学生ID访问
     */
    @GetMapping("/emoji/student/emoji-chat/{studentId}")
    public String emojiChat(@PathVariable String studentId, Model model) {
        // 添加必要的数据到模型中
        model.addAttribute("pageTitle", "表情交流室");
        model.addAttribute("studentId", studentId);
        // 默认显示第一个课程或所有课程
        model.addAttribute("currentCourseId", null);
        // 返回表情聊天页面的视图名称
        return "student/emoji-chat";
    }
    
    /**
     * 学生表情聊天页面 - 根据学生ID和课程ID访问
     */
    @GetMapping("/student/emoji-chat/{studentId}/{courseId}")
    public String emojiChatByCourse(@PathVariable String studentId, @PathVariable Long courseId, Model model) {
        // 添加必要的数据到模型中
        model.addAttribute("pageTitle", "表情交流室");
        model.addAttribute("studentId", studentId);
        model.addAttribute("currentCourseId", courseId);
        // 返回表情聊天页面的视图名称
        return "student/emoji-chat";
    }
}