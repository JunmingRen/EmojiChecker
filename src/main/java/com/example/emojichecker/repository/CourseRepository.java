package com.example.emojichecker.repository;

import com.example.emojichecker.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

/**
 * 课程数据访问接口
 * 提供课程相关的数据库操作方法
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * 根据教师用户名查询课程列表
     * @param teacherUsername 教师用户名
     * @return 课程列表
     */
    List<Course> findByTeacherUsername(String teacherUsername);
    
    /**
     * 根据教师用户名和星期几查询课程列表
     * @param teacherUsername 教师用户名
     * @param dayOfWeek 星期几
     * @return 课程列表
     */
    List<Course> findByTeacherUsernameAndDayOfWeek(String teacherUsername, DayOfWeek dayOfWeek);
    
    /**
     * 根据教师用户名和星期几查询课程列表，并按开始时间排序
     * @param teacherUsername 教师用户名
     * @param dayOfWeek 星期几
     * @return 按开始时间排序的课程列表
     */
    List<Course> findByTeacherUsernameAndDayOfWeekOrderByStartTimeAsc(String teacherUsername, DayOfWeek dayOfWeek);
    
    /**
     * 根据课程代码查询课程
     * @param courseCode 课程代码
     * @return 课程列表
     */
    List<Course> findByCourseCode(String courseCode);
    
    /**
     * 根据课程名称模糊查询
     * @param courseName 课程名称关键字
     * @return 课程列表
     */
    List<Course> findByCourseNameContaining(String courseName);
    
    /**
     * 查询活跃的课程
     * @param isActive 是否活跃
     * @return 课程列表
     */
    List<Course> findByIsActive(Boolean isActive);
    
    /**
     * 根据年份和学期查询课程
     * @param year 年份
     * @param semester 学期
     * @return 课程列表
     */
    List<Course> findByYearAndSemester(Integer year, String semester);
}