package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 课程实体类 - 映射到数据库表
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode; // 课程代码

    @Column(nullable = false)
    private String name; // 课程名称

    @Column
    private String description; // 课程描述

    @Column(name = "teacher_id", nullable = false)
    private String teacherId; // 授课教师ID

    // 构造函数
    public Course() {
    }

    public Course(String courseCode, String name, String description, String teacherId) {
        this.courseCode = courseCode;
        this.name = name;
        this.description = description;
        this.teacherId = teacherId;
    }

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}