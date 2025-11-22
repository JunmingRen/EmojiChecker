package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 数据迁移服务类 - 将users表中的用户数据迁移到对应的实体表
 */
@Service
public class DataMigrationService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    /**
     * 执行完整的数据迁移
     * 将users表中的教师和学生数据分别迁移到teachers和students表
     */
    @Transactional
    public MigrationResult migrateUsersToEntities() {
        System.out.println("开始执行数据迁移任务...");
        
        MigrationResult result = new MigrationResult();
        
        // 获取所有用户
        List<User> allUsers = userRepository.findAll();
        System.out.println("共找到 " + allUsers.size() + " 个用户需要处理");
        
        // 遍历用户并迁移数据
        for (User user : allUsers) {
            try {
                if ("teacher".equals(user.getRole())) {
                    // 迁移教师数据
                    boolean migrated = migrateTeacherData(user);
                    if (migrated) {
                        result.incrementTeacherCount();
                    }
                } else if ("student".equals(user.getRole())) {
                    // 迁移学生数据
                    boolean migrated = migrateStudentData(user);
                    if (migrated) {
                        result.incrementStudentCount();
                    }
                } else {
                    // 管理员用户不计入迁移统计
                    result.incrementAdminCount();
                }
            } catch (Exception e) {
                System.err.println("迁移用户 " + user.getUsername() + " 时出错: " + e.getMessage());
                result.addError(user.getUsername(), e.getMessage());
            }
        }
        
        System.out.println("数据迁移任务完成！");
        System.out.println("迁移结果: 成功迁移 " + result.getTeacherCount() + " 个教师, " 
                + result.getStudentCount() + " 个学生, 管理员用户 " + result.getAdminCount() + " 个");
        System.out.println("迁移过程中出错: " + result.getErrors().size() + " 个");
        
        return result;
    }
    
    /**
     * 迁移单个教师数据
     */
    private boolean migrateTeacherData(User user) {
        // 确保有有效的teacherId
        if (user.getTeacherId() == null || user.getTeacherId().isEmpty()) {
            String teacherId = generateTeacherId(user.getUsername());
            user.setTeacherId(teacherId);
            userRepository.save(user);
            System.out.println("为教师用户 " + user.getUsername() + " 生成了teacherId: " + teacherId);
        }
        
        // 检查教师记录是否已存在
        if (teacherRepository.existsByTeacherId(user.getTeacherId())) {
            System.out.println("教师记录已存在，跳过迁移: " + user.getUsername());
            return false;
        }
        
        // 创建教师记录
        Teacher teacher = new Teacher();
        teacher.setTeacherId(user.getTeacherId());
        teacher.setUsername(user.getUsername());
        teacher.setPassword(user.getPassword()); // 保留密码信息
        teacher.setName(user.getName());
        teacher.setAvatar(user.getAvatar());
        
        teacherRepository.save(teacher);
        System.out.println("成功迁移教师数据: " + user.getUsername() + " (" + user.getTeacherId() + ")");
        return true;
    }
    
    /**
     * 迁移单个学生数据
     */
    private boolean migrateStudentData(User user) {
        // 确保有有效的studentId
        if (user.getStudentId() == null || user.getStudentId().isEmpty()) {
            String studentId = generateStudentId(user.getUsername());
            user.setStudentId(studentId);
            userRepository.save(user);
            System.out.println("为学生用户 " + user.getUsername() + " 生成了studentId: " + studentId);
        }
        
        // 检查学生记录是否已存在
        if (studentRepository.existsByStudentId(user.getStudentId())) {
            System.out.println("学生记录已存在，跳过迁移: " + user.getUsername());
            return false;
        }
        
        // 创建学生记录
        Student student = new Student();
        student.setStudentId(user.getStudentId());
        student.setUsername(user.getUsername());
        student.setPassword(user.getPassword()); // 保留密码信息
        student.setName(user.getName());
        student.setAvatar(user.getAvatar());
        
        studentRepository.save(student);
        System.out.println("成功迁移学生数据: " + user.getUsername() + " (" + user.getStudentId() + ")");
        return true;
    }
    
    /**
     * 生成教师ID（T开头+6位数字）
     */
    private String generateTeacherId(String username) {
        // 从用户名提取数字部分
        String numStr = username.replaceAll("[^0-9]", "");
        // 如果没有数字，使用默认值
        if (numStr.isEmpty()) {
            numStr = "1";
        }
        return "T" + String.format("%06d", Integer.parseInt(numStr));
    }
    
    /**
     * 生成学生ID（S开头+6位数字）
     */
    private String generateStudentId(String username) {
        // 从用户名提取数字部分
        String numStr = username.replaceAll("[^0-9]", "");
        // 如果没有数字，使用默认值
        if (numStr.isEmpty()) {
            numStr = "1";
        }
        return "S" + String.format("%06d", Integer.parseInt(numStr));
    }
    
    /**
     * 验证迁移结果
     */
    public MigrationValidationResult validateMigration() {
        MigrationValidationResult validationResult = new MigrationValidationResult();
        
        // 验证教师数据
        List<User> teachers = userRepository.findByRole("teacher");
        for (User teacher : teachers) {
            Optional<Teacher> teacherOpt = teacherRepository.findByUsername(teacher.getUsername());
            if (!teacherOpt.isPresent()) {
                validationResult.addMissingTeacher(teacher.getUsername());
            }
        }
        
        // 验证学生数据
        List<User> students = userRepository.findByRole("student");
        for (User student : students) {
            Optional<Student> studentOpt = studentRepository.findByUsername(student.getUsername());
            if (!studentOpt.isPresent()) {
                validationResult.addMissingStudent(student.getUsername());
            }
        }
        
        return validationResult;
    }
    
    /**
     * 迁移结果内部类
     */
    public static class MigrationResult {
        private int teacherCount = 0;
        private int studentCount = 0;
        private int adminCount = 0;
        private java.util.Map<String, String> errors = new java.util.HashMap<>();
        
        public void incrementTeacherCount() { teacherCount++; }
        public void incrementStudentCount() { studentCount++; }
        public void incrementAdminCount() { adminCount++; }
        public void addError(String username, String errorMessage) { errors.put(username, errorMessage); }
        
        public int getTeacherCount() { return teacherCount; }
        public int getStudentCount() { return studentCount; }
        public int getAdminCount() { return adminCount; }
        public java.util.Map<String, String> getErrors() { return errors; }
    }
    
    /**
     * 迁移验证结果内部类
     */
    public static class MigrationValidationResult {
        private java.util.List<String> missingTeachers = new java.util.ArrayList<>();
        private java.util.List<String> missingStudents = new java.util.ArrayList<>();
        
        public void addMissingTeacher(String username) { missingTeachers.add(username); }
        public void addMissingStudent(String username) { missingStudents.add(username); }
        
        public java.util.List<String> getMissingTeachers() { return missingTeachers; }
        public java.util.List<String> getMissingStudents() { return missingStudents; }
        public boolean isSuccess() { return missingTeachers.isEmpty() && missingStudents.isEmpty(); }
    }
}