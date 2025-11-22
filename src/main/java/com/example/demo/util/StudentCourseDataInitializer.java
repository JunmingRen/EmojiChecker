package com.example.demo.util;

import com.example.demo.entity.Course;
import com.example.demo.entity.StudentCourse;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StudentCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 学生课程数据初始化工具类 - 用于为学生预设课程数据
 */
@Component
public class StudentCourseDataInitializer {

    private final StudentCourseRepository studentCourseRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public StudentCourseDataInitializer(StudentCourseRepository studentCourseRepository, CourseRepository courseRepository) {
        this.studentCourseRepository = studentCourseRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * 初始化所有测试学生的课程数据
     */
    public void initializeStudentCourses() {
        // 定义学生和他们应该选择的课程代码映射
        Map<String, List<String>> studentCoursesMapping = new HashMap<>();
        
        // 为每个学生分配不同的课程组合
        studentCoursesMapping.put("S000001", List.of("CS101-M", "MA102-M", "CS201-T")); // student1
        studentCoursesMapping.put("S000002", List.of("CS101-M", "PH101-T", "CS301-W")); // student2
        studentCoursesMapping.put("S000003", List.of("MA102-M", "CS201-T", "ENG101-W")); // student3
        studentCoursesMapping.put("S000004", List.of("CS201-T", "CS401-TH", "CS501-F")); // student4
        studentCoursesMapping.put("S000005", List.of("PH101-T", "MA202-TH", "CS601-F")); // student5
        
        // 初始化每个学生的课程
        for (Map.Entry<String, List<String>> entry : studentCoursesMapping.entrySet()) {
            String studentId = entry.getKey();
            List<String> courseCodes = entry.getValue();
            initializeStudentCourses(studentId, courseCodes);
        }
        
        System.out.println("所有学生课程数据初始化完成！");
    }
    
    /**
     * 为指定学生初始化课程数据
     * @param studentId 学生ID
     * @param courseCodes 课程代码列表
     */
    private void initializeStudentCourses(String studentId, List<String> courseCodes) {
        System.out.println("为学生" + studentId + "初始化课程...");
        
        // 获取所有课程
        List<Course> allCourses = courseRepository.findAll();
        
        // 创建学生课程关联列表
        List<StudentCourse> studentCourses = new ArrayList<>();
        
        // 查找对应课程并创建关联
        for (String courseCode : courseCodes) {
            // 查找课程
            Course course = allCourses.stream()
                .filter(c -> c.getCourseCode().equals(courseCode))
                .findFirst()
                .orElse(null);
            
            if (course != null) {
                // 检查是否已存在该关联
                if (!studentCourseRepository.existsByStudentIdAndCourseId(studentId, course.getId())) {
                    StudentCourse studentCourse = new StudentCourse(studentId, course.getId());
                    studentCourses.add(studentCourse);
                    System.out.println("已添加学生" + studentId + "的课程: " + course.getName());
                } else {
                    System.out.println("学生" + studentId + "已选修课程" + course.getName() + "，跳过添加");
                }
            } else {
                System.out.println("未找到课程代码为" + courseCode + "的课程，跳过添加");
            }
        }
        
        // 批量保存学生课程关联
        if (!studentCourses.isEmpty()) {
            studentCourseRepository.saveAll(studentCourses);
        }
    }
}