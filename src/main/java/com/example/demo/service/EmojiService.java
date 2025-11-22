package com.example.demo.service;

import com.example.demo.entity.EmojiMessage;
import com.example.demo.repository.EmojiMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.text.SimpleDateFormat;

/**
 * 表情服务类 - 处理表情相关的业务逻辑
 */
@Service
public class EmojiService {

    @Autowired
    private EmojiMessageRepository emojiMessageRepository;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 保存表情消息
     */
    public void saveEmojiMessage(String emoji, String studentName, String studentId, Long courseId) {
        EmojiMessage message = new EmojiMessage(emoji, studentName, studentId, courseId);
        emojiMessageRepository.save(message);
    }
    
    /**
     * 兼容旧版本的保存表情消息方法
     */
    public void saveEmojiMessage(String emoji, String studentName) {
        // 默认保存到课程ID为1的课程（向后兼容）
        saveEmojiMessage(emoji, studentName, "default", 1L);
    }

    /**
     * 获取所有表情消息，转换为前端需要的格式
     */
    public List<Map<String, String>> getAllEmojiMessages() {
        List<EmojiMessage> messages = emojiMessageRepository.findTop100ByOrderByCreatedAtDesc();
        return convertMessagesToMap(messages);
    }
    
    /**
     * 根据课程ID获取表情消息
     */
    public List<Map<String, String>> getEmojiMessagesByCourseId(Long courseId) {
        List<EmojiMessage> messages = emojiMessageRepository.findTop100ByCourseIdOrderByCreatedAtDesc(courseId);
        return convertMessagesToMap(messages);
    }
    
    /**
     * 辅助方法：将表情消息列表转换为前端需要的格式
     */
    private List<Map<String, String>> convertMessagesToMap(List<EmojiMessage> messages) {
        List<Map<String, String>> result = new ArrayList<>();
        
        // 反转列表，使最新的消息在最后
        Collections.reverse(messages);
        
        for (EmojiMessage message : messages) {
            Map<String, String> msgMap = new HashMap<>();
            msgMap.put("emoji", message.getEmoji());
            msgMap.put("studentName", message.getStudentName());
            msgMap.put("studentId", message.getStudentId());
            msgMap.put("time", message.getCreatedAt().format(timeFormatter));
            result.add(msgMap);
        }
        
        return result;
    }

    /**
     * 获取表情统计数据
     */
    public Map<String, Integer> getEmojiStats() {
        List<Object[]> results = emojiMessageRepository.countEmojis();
        Map<String, Integer> stats = new HashMap<>();
        
        for (Object[] result : results) {
            String emoji = (String) result[0];
            Long count = (Long) result[1];
            stats.put(emoji, count.intValue());
        }
        
        return stats;
    }
    
    /**
     * 根据课程ID获取表情统计数据
     */
    public Map<String, Integer> getEmojiStatsByCourseId(Long courseId) {
        List<Object[]> results = emojiMessageRepository.countEmojisByCourseId(courseId);
        Map<String, Integer> stats = new HashMap<>();
        
        for (Object[] result : results) {
            String emoji = (String) result[0];
            Long count = (Long) result[1];
            stats.put(emoji, count.intValue());
        }
        
        return stats;
    }

    /**
     * 获取最近的表情消息
     */
    public List<Map<String, String>> getRecentEmojis(int limit) {
        // 使用仓库中正确的方法名
        List<EmojiMessage> messages = emojiMessageRepository.findTop100ByOrderByCreatedAtDesc();
        // 限制返回的记录数量
        List<Map<String, String>> recentEmojis = new ArrayList<>();
        
        int actualLimit = Math.min(limit, messages.size());
        for (int i = 0; i < actualLimit; i++) {
            EmojiMessage message = messages.get(i);
            Map<String, String> msgMap = new HashMap<>();
            msgMap.put("emoji", message.getEmoji());
            msgMap.put("studentName", message.getStudentName());
            msgMap.put("time", message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            recentEmojis.add(msgMap);
        }
        
        return recentEmojis;
    }
    
    /**
     * 根据课程ID获取最近的表情消息
     */
    public List<Map<String, String>> getRecentEmojisByCourseId(int limit, Long courseId) {
        List<EmojiMessage> messages = emojiMessageRepository.findTop100ByCourseIdOrderByCreatedAtDesc(courseId);
        List<Map<String, String>> recentEmojis = new ArrayList<>();
        
        int actualLimit = Math.min(limit, messages.size());
        for (int i = 0; i < actualLimit; i++) {
            EmojiMessage message = messages.get(i);
            Map<String, String> msgMap = new HashMap<>();
            msgMap.put("emoji", message.getEmoji());
            msgMap.put("studentName", message.getStudentName());
            msgMap.put("time", message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            recentEmojis.add(msgMap);
        }
        
        return recentEmojis;
    }
    
    /**
     * 管理员功能：获取表情统计数据（支持筛选）
     */
    public Map<String, Object> getAdminEmojiStats(String dateRange, String emojiType) {
        Map<String, Object> stats = new HashMap<>();
        
        // 基础统计
        List<Object[]> results = emojiMessageRepository.countEmojis();
        Map<String, Integer> emojiCounts = new HashMap<>();
        int totalMessages = 0;
        
        for (Object[] result : results) {
            String emoji = (String) result[0];
            Long count = (Long) result[1];
            emojiCounts.put(emoji, count.intValue());
            totalMessages += count;
        }
        
        // 按日期统计（最近7天）
        Map<String, Integer> dailyStats = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dailyStats.put(dateStr, 0); // 初始化为0
        }
        
        // 填充实际数据（这里简化处理，实际项目中应该从数据库查询）
        List<EmojiMessage> recentMessages = emojiMessageRepository.findTop1000ByOrderByCreatedAtDesc();
        for (EmojiMessage msg : recentMessages) {
            String dateStr = msg.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (dailyStats.containsKey(dateStr)) {
                dailyStats.put(dateStr, dailyStats.get(dateStr) + 1);
            }
        }
        
        stats.put("totalMessages", totalMessages);
        stats.put("uniqueEmojis", emojiCounts.size());
        stats.put("emojiCounts", emojiCounts);
        stats.put("dailyStats", dailyStats);
        stats.put("topEmoji", getTopEmoji(emojiCounts));
        
        return stats;
    }
    
    /**
     * 获取表情列表（用于管理员页面）
     */
    public List<Map<String, Object>> getEmojiListForAdmin(String dateRange, String emojiType) {
        List<EmojiMessage> messages = emojiMessageRepository.findTop1000ByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 简单过滤实现
        for (EmojiMessage message : messages) {
            // 根据emojiType过滤
            if (!"all".equals(emojiType) && !emojiType.equals(message.getEmoji())) {
                continue;
            }
            
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("id", message.getId());
            msgMap.put("emoji", message.getEmoji());
            msgMap.put("studentName", message.getStudentName());
            msgMap.put("createdAt", message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.add(msgMap);
        }
        
        return result;
    }
    
    /**
     * 导出表情数据为CSV
     */
    public String exportEmojiDataToCsv(String dateRange, String emojiType) {
        StringBuilder csvBuilder = new StringBuilder();
        // CSV 表头
        csvBuilder.append("ID,表情,学生姓名,发送时间\n");
        
        List<Map<String, Object>> emojiList = getEmojiListForAdmin(dateRange, emojiType);
        
        for (Map<String, Object> emoji : emojiList) {
            csvBuilder.append(emoji.get("id")).append(",")
                     .append(escapeCsvValue(String.valueOf(emoji.get("emoji")))).append(",")
                     .append(escapeCsvValue(String.valueOf(emoji.get("studentName")))).append(",")
                     .append(emoji.get("createdAt")).append("\n");
        }
        
        return csvBuilder.toString();
    }
    
    /**
     * 导出表情数据为PDF（简化实现）
     */
    public byte[] exportEmojiDataToPdf(String dateRange, String emojiType) {
        try {
            // 为了简化实现，这里返回一个占位的字节数组
            // 实际项目中应使用PDF生成库如iText
            String content = "表情数据导出PDF报告\n" +
                             "日期范围: " + dateRange + "\n" +
                             "表情类型: " + emojiType + "\n" +
                             "生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(content.getBytes());
            return outputStream.toByteArray();
        } catch (Exception e) {
            return new byte[0]; // 出错时返回空字节数组
        }
    }
    
    /**
     * 辅助方法：获取出现次数最多的表情
     */
    private String getTopEmoji(Map<String, Integer> emojiCounts) {
        return emojiCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }
    
    /**
     * 辅助方法：转义CSV值
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}