package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * 学生课程关联实体类 - 映射到数据库表
 * 用于表示学生和课程之间的多对多关系
 */
@Entity
@Table(name = "student_courses")
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    // 构造函数
    public StudentCourse() {
    }

    public StudentCourse(String studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}