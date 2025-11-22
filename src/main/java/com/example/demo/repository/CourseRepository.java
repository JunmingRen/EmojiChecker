package com.example.demo.repository;

import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程仓库接口 - 用于课程数据的CRUD操作
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 根据课程代码查找课程
     * @param courseCode 课程代码
     * @return 课程对象或空
     */
    Optional<Course> findByCourseCode(String courseCode);

    /**
     * 根据教师ID查找课程
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> findByTeacherId(String teacherId);

    /**
     * 检查课程代码是否存在
     * @param courseCode 课程代码
     * @return 是否存在
     */
    boolean existsByCourseCode(String courseCode);
}