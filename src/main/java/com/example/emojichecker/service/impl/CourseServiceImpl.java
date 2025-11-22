package com.example.emojichecker.service.impl;

import com.example.emojichecker.model.Course;
import com.example.emojichecker.service.CourseService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Override
    public List<Course> getCoursesByTeacherUsername(String teacherUsername) {
        return getMockCourses(teacherUsername);
    }

    @Override
    public Course getCourseById(Long id) {
        return null;
    }

    @Override
    public List<Course> getCoursesByDayOfWeek(String teacherUsername, DayOfWeek dayOfWeek) {
        return getMockCourses(teacherUsername);
    }

    @Override
    public List<Course> getCoursesByDayOfWeekAndSortByTime(String teacherUsername, DayOfWeek dayOfWeek) {
        return getMockCourses(teacherUsername);
    }

    @Override
    public List<Course> searchCourses(String teacherUsername, String keyword) {
        return getMockCourses(teacherUsername);
    }

    /**
     * 生成模拟课程数据
     */
    private List<Course> getMockCourses(String teacherUsername) {
        // 返回空列表以避免初始化问题
        return new ArrayList<>();
    }
}