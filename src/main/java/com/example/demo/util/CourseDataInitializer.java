package com.example.demo.util;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据初始化工具类 - 用于为Teacher1创建一周的课程数据
 */
@Component
public class CourseDataInitializer {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseDataInitializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * 为Teacher1初始化一周的课程数据
     */
    public void initializeTeacher1Courses() {
        // 教师ID
        String teacherId = "Teacher1";
        
        // 创建课程列表
        List<Course> courses = new ArrayList<>();
        
        // 周一课程
        courses.add(new Course("CS101-M", "计算机科学导论-周一班", "计算机科学基础知识与应用导论，适合初学者", teacherId));
        courses.add(new Course("MA102-M", "高等数学-周一班", "高等数学基础课程，包含微积分、线性代数等内容", teacherId));
        
        // 周二课程
        courses.add(new Course("CS201-T", "数据结构与算法-周二班", "数据结构与算法基础，包含常见数据结构及其应用", teacherId));
        courses.add(new Course("PH101-T", "物理基础-周二班", "物理学科基础知识，包含力学、电磁学等内容", teacherId));
        
        // 周三课程
        courses.add(new Course("CS301-W", "操作系统原理-周三班", "操作系统基本原理与实现技术", teacherId));
        courses.add(new Course("ENG101-W", "专业英语-周三班", "计算机专业英语，提升技术阅读与表达能力", teacherId));
        
        // 周四课程
        courses.add(new Course("CS401-TH", "计算机网络-周四班", "计算机网络基础与应用，包含网络协议、网络安全等", teacherId));
        courses.add(new Course("MA202-TH", "概率论与数理统计-周四班", "概率论与数理统计基础，为数据分析提供理论支持", teacherId));
        
        // 周五课程
        courses.add(new Course("CS501-F", "数据库原理与应用-周五班", "数据库系统原理与应用技术", teacherId));
        courses.add(new Course("CS601-F", "软件工程基础-周五班", "软件工程基本原理与实践方法", teacherId));
        
        // 保存课程到数据库
        for (Course course : courses) {
            // 检查课程代码是否已存在，避免重复添加
            if (!courseRepository.existsByCourseCode(course.getCourseCode())) {
                courseRepository.save(course);
                System.out.println("已添加课程: " + course.getName());
            } else {
                System.out.println("课程已存在，跳过添加: " + course.getName());
            }
        }
        
        System.out.println("Teacher1课程数据初始化完成！");
    }
}