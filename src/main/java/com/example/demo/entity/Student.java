package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * 学生实体类 - 映射到数据库表
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId; // 学生ID，格式：Sxxxxxx，作为主键

    @Column(nullable = false)
    private String username; // 用户名

    @Column(nullable = false)
    private String password; // 密码

    @Column(nullable = false)
    private String name; // 真实姓名

    @Column
    private String avatar; // 头像URL

    // 构造函数
    public Student() {
    }

    public Student(String studentId, String username, String password, String name) {
        this.studentId = studentId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.avatar = "https://picsum.photos/id/" + (1050 + (int)(Math.random() * 100)) + "/100/100";
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}