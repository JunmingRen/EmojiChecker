package com.example.demo.repository;

import com.example.demo.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生课程关联仓库接口 - 用于学生课程关联数据的CRUD操作
 */
@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    /**
     * 根据学生ID查找所选课程
     * @param studentId 学生ID
     * @return 学生课程关联列表
     */
    List<StudentCourse> findByStudentId(String studentId);

    /**
     * 根据课程ID查找选课学生
     * @param courseId 课程ID
     * @return 学生课程关联列表
     */
    List<StudentCourse> findByCourseId(Long courseId);

    /**
     * 检查学生是否已选该课程
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否存在
     */
    boolean existsByStudentIdAndCourseId(String studentId, Long courseId);
}