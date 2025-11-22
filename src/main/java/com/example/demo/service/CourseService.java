package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 课程服务类 - 处理课程相关的业务逻辑
 */
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * 根据教师ID查找所有课程
     * @param teacherId 教师ID
     * @return 课程列表
     */
    public List<Course> getCoursesByTeacherId(String teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    /**
     * 获取教师当前正在进行的课程
     * 这里简化处理，返回教师的第一个课程
     * 实际项目中应该根据时间安排判断
     * @param teacherId 教师ID
     * @return 当前课程（如果有）
     */
    public Optional<Course> getCurrentCourseByTeacher(String teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        // 在实际项目中，这里应该根据当前时间和课程表判断哪个课程正在进行
        // 这里简化处理，返回第一个课程
        return courses.isEmpty() ? Optional.empty() : Optional.of(courses.get(0));
    }

    /**
     * 检查教师是否有课程
     * @param teacherId 教师ID
     * @return 是否有课程
     */
    public boolean hasCourses(String teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        return !courses.isEmpty();
    }

    /**
     * 根据课程ID获取课程信息
     * @param courseId 课程ID
     * @return 课程对象
     */
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }
}