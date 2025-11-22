package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器 - 处理各角色页面路由和用户认证
 */
@Controller
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 显示登录页面
     */
    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }
    
    /**
     * 处理登录请求
     * 根据用户名、密码和角色进行认证
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, @RequestParam String role, Model model) {
        // 添加日志记录，调试问题
        System.out.println("登录请求参数 - 用户名: " + username + ", 提交角色: " + role);
        
        // 验证用户信息并获取重定向路径
        String redirectPath = null;
        
        // 先从User表查询用户
        User user = userService.findByUsername(username).orElse(null);
        
        if (user == null) {
            model.addAttribute("error", "用户名或密码错误，请重试");
            return "login";
        }
        
        // 验证角色是否匹配
        if (!role.equals(user.getRole())) {
            model.addAttribute("error", "用户角色不匹配，请选择正确的角色");
            return "login";
        }
        
        // 验证密码 - 使用加密验证功能
        if (!userService.validatePassword(password, user.getPassword())) {
            model.addAttribute("error", "用户名或密码错误，请重试");
            return "login";
        }
        
        // 根据角色确定重定向路径
        if ("teacher".equals(role) && user.getTeacherId() != null) {
            redirectPath = "/teacher/dashboard/" + user.getTeacherId();
            System.out.println("教师登录成功: " + username + " (" + user.getTeacherId() + ")");
        } else if ("student".equals(role) && user.getStudentId() != null) {
            redirectPath = "/student/dashboard/" + user.getStudentId();
            System.out.println("学生登录成功: " + username + " (" + user.getStudentId() + ")");
        } else if ("admin".equals(role)) {
            redirectPath = "/admin/dashboard";
            System.out.println("管理员登录成功: " + username);
        }
        
        if (redirectPath != null) {
            System.out.println("登录成功，重定向到: " + redirectPath);
            return "redirect:" + redirectPath;
        } else {
            // 登录失败，添加错误信息
            System.out.println("登录失败 - 用户信息不完整，请联系管理员 - 用户名: " + username + ", 角色: " + role);
            model.addAttribute("error", "用户信息不完整，请联系管理员");
            return "login";
        }
    }
    
    // 注册页面映射已在UserController中定义
    
    /**
     * 显示教师仪表盘（支持带ID参数）
     */
    @GetMapping({"/teacher/dashboard", "/teacher/dashboard/{teacherId}"})
    public String showTeacherDashboard(@PathVariable(required = false) String teacherId, Model model) {
        // 确保在任何情况下都有teacherId属性
        model.addAttribute("teacherId", teacherId);
        
        // 如果提供了教师ID，根据ID获取教师信息
        if (teacherId != null) {
            System.out.println("访问教师仪表盘，教师ID: " + teacherId);
            
            try {
                // 从新的Teacher表获取教师信息
                Teacher teacher = userService.findByTeacherId(teacherId).orElse(null);
                if (teacher != null) {
                    model.addAttribute("teacher", teacher);
                    System.out.println("成功获取教师信息: " + teacher.getName() + " (" + teacher.getUsername() + ")");
                } else {
                    System.out.println("未找到教师ID: " + teacherId + " 的信息");
                    // 设置默认教师对象，避免前端访问undefined属性
                    model.addAttribute("teacher", new Teacher());
                }
            } catch (Exception e) {
                System.out.println("获取教师信息时出错: " + e.getMessage());
                e.printStackTrace();
                // 异常情况下也确保teacher属性存在
                model.addAttribute("teacher", new Teacher());
            }
        } else {
            // 没有提供teacherId时，也确保teacher属性存在
            model.addAttribute("teacher", new Teacher());
        }
        
        // 确保其他可能被前端JavaScript使用的必要属性也存在
        if (!model.containsAttribute("courses")) {
            model.addAttribute("courses", List.of());
        }
        if (!model.containsAttribute("students")) {
            model.addAttribute("students", List.of());
        }
        if (!model.containsAttribute("emojiData")) {
            model.addAttribute("emojiData", List.of());
        }
        
        return "teacher/dashboard";
    }
    
    @GetMapping("/teacher/emoji-monitor")
    public String showTeacherEmojiMonitor() {
        return "teacher/emoji-monitor";
    }
    
    /**
     * 显示教师课程列表页面
     * 与仪表盘URL格式保持一致，使用teacherId作为路径参数
     */
    @GetMapping({"/teacher/courses", "/teacher/courses/{teacherId}"})
    public String showTeacherCourses(@PathVariable(required = false) String teacherId, Model model) {
        // 确保teacherId属性存在
        model.addAttribute("teacherId", teacherId);
        
        // 如果提供了教师ID，根据ID获取教师信息和课程数据
        if (teacherId != null) {
            System.out.println("访问教师课程列表，教师ID: " + teacherId);
            
            try {
                // 获取教师信息
                Teacher teacher = userService.findByTeacherId(teacherId).orElse(null);
                if (teacher != null) {
                    model.addAttribute("teacher", teacher);
                    System.out.println("成功获取教师信息: " + teacher.getName() + " (" + teacher.getUsername() + ")");
                } else {
                    System.out.println("未找到教师ID: " + teacherId + " 的信息");
                    model.addAttribute("teacher", new Teacher());
                }
                
                // 获取模拟课程数据
                // 后续可以从数据库获取实际课程数据
                List<Map<String, Object>> courses = new ArrayList<>();
                courses.add(Map.of("courseId", 1, "courseName", "软件工程导论", "courseCode", "CS301", "studentCount", 45, "status", "进行中"));
                courses.add(Map.of("courseId", 2, "courseName", "数据结构", "courseCode", "CS203", "studentCount", 38, "status", "进行中"));
                courses.add(Map.of("courseId", 3, "courseName", "操作系统", "courseCode", "CS305", "studentCount", 52, "status", "未开始"));
                courses.add(Map.of("courseId", 4, "courseName", "数据库原理", "courseCode", "CS310", "studentCount", 41, "status", "进行中"));
                
                model.addAttribute("courses", courses);
                
            } catch (Exception e) {
                System.out.println("获取教师课程信息时出错: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("teacher", new Teacher());
                model.addAttribute("courses", List.of());
                model.addAttribute("error", "加载课程信息时出错");
            }
        } else {
            // 没有提供teacherId时的默认值
            model.addAttribute("teacher", new Teacher());
            
            // 同样添加模拟课程数据
            List<Map<String, Object>> courses = new ArrayList<>();
            courses.add(Map.of("courseId", 1, "courseName", "软件工程导论", "courseCode", "CS301", "studentCount", 45, "status", "进行中"));
            courses.add(Map.of("courseId", 2, "courseName", "数据结构", "courseCode", "CS203", "studentCount", 38, "status", "进行中"));
            courses.add(Map.of("courseId", 3, "courseName", "操作系统", "courseCode", "CS305", "studentCount", 52, "status", "未开始"));
            courses.add(Map.of("courseId", 4, "courseName", "数据库原理", "courseCode", "CS310", "studentCount", 41, "status", "进行中"));
            
            model.addAttribute("courses", courses);
        }
        
        return "teacher/courses";
    }
    
    /**
     * 显示学生仪表盘（支持带ID参数）
     */
    @GetMapping({"/student/dashboard", "/student/dashboard/{studentId}"})
    public String showStudentDashboard(@PathVariable(required = false) String studentId, Model model) {
        // 如果提供了学生ID，根据ID获取学生信息
        if (studentId != null) {
            model.addAttribute("studentId", studentId);
            System.out.println("访问学生仪表盘，学生ID: " + studentId);
            
            // 从新的Student表获取学生信息
            Student student = userService.findByStudentId(studentId).orElse(null);
            if (student != null) {
                model.addAttribute("student", student);
                System.out.println("成功获取学生信息: " + student.getName() + " (" + student.getUsername() + ")");
            } else {
                System.out.println("未找到学生ID: " + studentId + " 的信息");
            }
        }
        return "student/dashboard";
    }
    
    @GetMapping("/student/emoji-selector")
    public String showStudentEmojiSelector() {
        return "student/emoji-selector";
    }
    
    @GetMapping("/student/emoji-chat/{studentId}")
    public String showStudentEmojiChat(@PathVariable(required = false) String studentId, @RequestParam(required = false) Long courseId, Model model) {
        try {
            // 根据学生ID获取学生信息
            if (studentId != null) {
                model.addAttribute("studentId", studentId);
                System.out.println("访问表情聊天室，学生ID: " + studentId + ", 课程ID: " + courseId);
                
                // 从Student表获取学生信息
                Student student = userService.findByStudentId(studentId).orElse(null);
                if (student != null) {
                    model.addAttribute("student", student);
                    model.addAttribute("studentName", student.getName());
                    System.out.println("成功获取学生信息: " + student.getName() + " (" + student.getUsername() + ")");
                } else {
                    System.out.println("未找到学生ID: " + studentId + " 的信息，使用默认值");
                    model.addAttribute("studentName", "学生");
                }
            } else {
                model.addAttribute("studentName", "学生");
            }
            
            // 添加学生ID和课程ID参数
            model.addAttribute("studentId", studentId);
            System.out.println("表情聊天室加载学生ID: " + studentId);
            
            if (courseId != null) {
                model.addAttribute("currentCourseId", courseId);
                System.out.println("表情聊天室加载课程ID: " + courseId);
            } else {
                model.addAttribute("currentCourseId", null);
            }
            
            return "student/emoji-chat";
        } catch (Exception e) {
            System.out.println("访问表情聊天室页面出错: " + e.getMessage());
            e.printStackTrace();
            // 出错时仍然返回页面，但提供错误信息
            model.addAttribute("studentName", "学生");
            model.addAttribute("studentId", studentId);
            model.addAttribute("currentCourseId", courseId != null ? courseId : null);
            model.addAttribute("error", "加载页面时出错，但仍可使用基础功能");
            return "student/emoji-chat";
        }
    }
    
    /**
     * 显示管理员仪表盘
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {
        return "admin/dashboard";
    }
    
    // 管理员用户管理页面
    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }
    
    // 管理员表情数据页面
    @GetMapping("/admin/emoji-data")
    public String adminEmojiData() {
        return "admin/emoji-data";
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
    
    // 管理员API：获取所有用户数据
    @GetMapping("/api/admin/users")
    @ResponseBody
    public Map<String, Object> getAdminUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "all") String role,
            @RequestParam(defaultValue = "") String search) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> users;
            
            // 根据角色筛选
            if ("all".equals(role)) {
                users = userService.findAll();
            } else {
                // 将Iterable转换为List
                users = new ArrayList<>();
                userService.findByRole(role).forEach(users::add);
            }
            
            // 根据搜索关键词过滤
            if (!search.isEmpty()) {
                users = users.stream()
                        .filter(user -> user.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                user.getName().toLowerCase().contains(search.toLowerCase()))
                        .toList();
            }
            
            // 计算总数
            int total = users.size();
            
            // 分页处理
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<User> paginatedUsers = (start < total) ? users.subList(start, end) : List.of();
            
            response.put("status", "success");
            response.put("users", paginatedUsers);
            response.put("total", total);
            response.put("page", page);
            response.put("pageSize", pageSize);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "获取用户数据失败: " + e.getMessage());
        }
        
        return response;
    }
}