package com.example.emojichecker.service;

import com.example.emojichecker.model.Course;
import java.time.DayOfWeek;
import java.util.List;

public interface CourseService {
    
    List<Course> getCoursesByTeacherUsername(String teacherUsername);
    
    Course getCourseById(Long id);
    
    List<Course> getCoursesByDayOfWeek(String teacherUsername, DayOfWeek dayOfWeek);
    
    List<Course> getCoursesByDayOfWeekAndSortByTime(String teacherUsername, DayOfWeek dayOfWeek);
    
    List<Course> searchCourses(String teacherUsername, String keyword);
}