package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * 用户实体类 - 映射到数据库表
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // teacher, student, admin

    @Column
    private String name; // 真实姓名

    @Column
    private String avatar; // 头像URL
    
    @Column(name = "teacher_id", unique = true)
    private String teacherId; // 教师ID，格式：Txxxxxx
    
    @Column(name = "student_id", unique = true)
    private String studentId; // 学生ID，格式：Sxxxxxx

    // 构造函数
    public User() {
    }

    public User(String username, String password, String role, String name) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.avatar = "https://picsum.photos/id/" + (1000 + (int)(Math.random() * 100)) + "/100/100";
        // 初始化ID字段为null，将在服务层生成
        this.teacherId = null;
        this.studentId = null;
    }

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
    
    public String getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}