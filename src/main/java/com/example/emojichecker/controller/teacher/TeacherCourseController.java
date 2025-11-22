package com.example.emojichecker.controller.teacher;

import com.example.emojichecker.model.Course;
import com.example.emojichecker.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

/**
 * 教师课程控制器
 * 处理教师相关的课程管理请求
 */
@Controller
@RequestMapping("/teacher")
public class TeacherCourseController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 跳转到老师课程表页面（支持带ID参数）
     * @param teacherId 教师ID参数
     * @param model 模型对象，用于存储页面数据
     * @param principal 当前登录用户信息
     * @return 课程表页面模板
     */
    @GetMapping({"/courses", "/courses/{teacherId}"})
    public String getTeacherCourses(@PathVariable(required = false) String teacherId, Model model, Principal principal) {
        // 如果提供了教师ID，根据ID获取教师信息
        if (teacherId != null) {
            model.addAttribute("teacherId", teacherId);
            System.out.println("访问教师课程表，教师ID: " + teacherId);
            
            // 从com.example.demo包导入Teacher服务，获取教师信息
            try {
                // 使用反射获取UserService的实例并调用findByTeacherId方法
                Class<?> userServiceClass = Class.forName("com.example.demo.service.UserService");
                Object userService = applicationContext.getBean(userServiceClass);
                
                // 调用findByTeacherId方法
                java.lang.reflect.Method method = userServiceClass.getMethod("findByTeacherId", String.class);
                java.util.Optional<?> optionalTeacher = (java.util.Optional<?>) method.invoke(userService, teacherId);
                
                if (optionalTeacher.isPresent()) {
                    Object teacher = optionalTeacher.get();
                    model.addAttribute("teacher", teacher);
                    
                    // 获取教师名称并打印
                    java.lang.reflect.Method getNameMethod = teacher.getClass().getMethod("getName");
                    String teacherName = (String) getNameMethod.invoke(teacher);
                    System.out.println("成功获取教师信息: " + teacherName);
                    
                    // 获取教师的所有课程
                    List<Course> courses = courseService.getCoursesByTeacherUsername(teacherName);
                    model.addAttribute("courses", courses);
                } else {
                    System.out.println("未找到教师ID: " + teacherId + " 的信息");
                    model.addAttribute("courses", List.of());
                }
            } catch (Exception e) {
                System.out.println("获取教师信息时出错: " + e.getMessage());
                e.printStackTrace();
                // 异常情况下也确保courses属性存在
                model.addAttribute("courses", List.of());
            }
        } else if (principal != null) {
            // 如果没有提供教师ID但有登录用户，使用用户名
            String teacherUsername = principal.getName();
            model.addAttribute("teacher", new Object() {
                public String getName() {
                    return teacherUsername;
                }
                public String getAvatar() {
                    return null;
                }
            });
            
            // 获取教师的所有课程
            List<Course> courses = courseService.getCoursesByTeacherUsername(teacherUsername);
            model.addAttribute("courses", courses);
        } else {
            // 默认返回空列表
            model.addAttribute("courses", List.of());
        }
        
        // 返回课程表页面
        return "teacher/courses";
    }
    
    /**
     * 获取课程详情
     * @param courseId 课程ID
     * @param model 模型对象
     * @return 课程详情页面
     */
    @GetMapping("/course-detail")
    public String getCourseDetail(@RequestParam("id") Long courseId, Model model) {
        // 获取课程详情
        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        return "teacher/course-detail";
    }
}