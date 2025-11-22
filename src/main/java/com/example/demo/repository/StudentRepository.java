package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 学生仓库接口 - 用于学生数据的CRUD操作
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    /**
     * 根据用户名查找学生
     * @param username 用户名
     * @return 学生对象或空
     */
    Optional<Student> findByUsername(String username);

    /**
     * 根据学生ID查找学生
     * @param studentId 学生ID
     * @return 学生对象或空
     */
    Optional<Student> findByStudentId(String studentId);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查学生ID是否存在
     * @param studentId 学生ID
     * @return 是否存在
     */
    boolean existsByStudentId(String studentId);
}