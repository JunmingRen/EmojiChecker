package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 表情消息实体类 - 映射到数据库表
 */
@Entity
@Table(name = "emoji_messages")
public class EmojiMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emoji;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 构造函数
    public EmojiMessage() {
        this.createdAt = LocalDateTime.now();
    }

    public EmojiMessage(String emoji, String studentName, String studentId, Long courseId) {
        this.emoji = emoji;
        this.studentName = studentName;
        this.studentId = studentId;
        this.courseId = courseId;
        this.createdAt = LocalDateTime.now();
    }

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}