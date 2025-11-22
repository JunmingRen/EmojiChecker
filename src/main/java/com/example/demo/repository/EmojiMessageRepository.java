package com.example.demo.repository;

import com.example.demo.entity.EmojiMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Map;

/**
 * 表情消息数据访问接口
 */
public interface EmojiMessageRepository extends JpaRepository<EmojiMessage, Long> {

    /**
     * 获取最新的表情消息，按时间倒序排列
     */
    List<EmojiMessage> findAllByOrderByCreatedAtDesc();

    /**
     * 获取最新的N条表情消息
     */
    List<EmojiMessage> findTop100ByOrderByCreatedAtDesc();
    
    /**
     * 获取最新的1000条表情消息（用于管理员统计）
     */
    List<EmojiMessage> findTop1000ByOrderByCreatedAtDesc();
    
    /**
     * 根据课程ID获取最新的表情消息
     */
    List<EmojiMessage> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    
    /**
     * 根据课程ID获取最新的N条表情消息
     */
    List<EmojiMessage> findTop100ByCourseIdOrderByCreatedAtDesc(Long courseId);
    
    /**
     * 根据课程ID获取表情统计数据
     */
    @Query(value = "SELECT emoji, COUNT(*) as count FROM emoji_messages WHERE course_id = :courseId GROUP BY emoji", nativeQuery = true)
    List<Object[]> countEmojisByCourseId(Long courseId);

    /**
     * 获取表情统计数据
     */
    @Query(value = "SELECT emoji, COUNT(*) as count FROM emoji_messages GROUP BY emoji", nativeQuery = true)
    List<Object[]> countEmojis();
}