package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ResponseBody;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 教师控制器 - 处理教师页面的请求
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    /**
     * 教师仪表盘页面
     * @return 仪表盘视图，包含课程列表
     */
    @GetMapping("/")
    public String teacherDashboard(Model model) {
        // 获取当前登录用户信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String username = userDetails.getUsername();
            model.addAttribute("username", username);
        }
        
        // 添加模拟课程数据
        model.addAttribute("courses", getMockCourses());
        
        return "teacher/dashboard";
    }

    /**
     * 获取教师当前正在进行的课程
     * @param principal 当前登录用户
     * @return 包含课程信息的响应
     */
    @GetMapping("/current-course")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentCourse(Principal principal) {
        // 这里应该调用服务层获取当前进行中的课程
        // 暂时返回模拟数据
        Map<String, Object> response = new HashMap<>();
        response.put("hasCourse", true);
        response.put("courseId", 1);
        response.put("courseName", "软件工程导论");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 处理教师访问表情聊天页面的请求
     * @param courseId 课程ID
     * @return 表情聊天页面视图，附带课程ID参数
     */
    @GetMapping("/emoji-chat")
    public String emojiChat(Model model, Integer courseId) {
        // 这里应该根据courseId获取课程信息
        if (courseId != null) {
            model.addAttribute("courseId", courseId);
            model.addAttribute("courseName", getCourseNameById(courseId));
        } else {
            model.addAttribute("courseId", 1);
            model.addAttribute("courseName", "默认课程");
        }
        return "teacher/emoji-chat";
    }
    
    // 内部类：课程信息
    public static class CourseInfo {
        private int id;
        private String name;
        private String code;
        private int studentCount;
        private String status;
        
        public CourseInfo(int id, String name, String code, int studentCount, String status) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.studentCount = studentCount;
            this.status = status;
        }
        
        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getCode() { return code; }
        public int getStudentCount() { return studentCount; }
        public String getStatus() { return status; }
    }
    
    // 获取模拟课程数据
    private List<CourseInfo> getMockCourses() {
        return List.of(
            new CourseInfo(1, "软件工程导论", "CS301", 45, "进行中"),
            new CourseInfo(2, "数据结构", "CS203", 38, "进行中"),
            new CourseInfo(3, "操作系统", "CS305", 52, "未开始"),
            new CourseInfo(4, "数据库原理", "CS310", 41, "进行中")
        );
    }
    
    // 根据ID获取课程名称（模拟）
    private String getCourseNameById(int courseId) {
        List<CourseInfo> courses = getMockCourses();
        for (CourseInfo course : courses) {
            if (course.getId() == courseId) {
                return course.getName();
            }
        }
        return "未知课程";
    }
}