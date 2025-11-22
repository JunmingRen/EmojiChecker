package com.example.demo.repository;

import com.example.demo.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 教师仓库接口 - 用于教师数据的CRUD操作
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {

    /**
     * 根据用户名查找教师
     * @param username 用户名
     * @return 教师对象或空
     */
    Optional<Teacher> findByUsername(String username);

    /**
     * 根据教师ID查找教师
     * @param teacherId 教师ID
     * @return 教师对象或空
     */
    Optional<Teacher> findByTeacherId(String teacherId);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查教师ID是否存在
     * @param teacherId 教师ID
     * @return 是否存在
     */
    boolean existsByTeacherId(String teacherId);
}