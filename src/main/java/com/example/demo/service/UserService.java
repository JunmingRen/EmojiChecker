package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户服务类 - 处理用户相关的业务逻辑
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        String teacherId = "T" + String.format("%06d", Integer.parseInt(numStr));
        System.out.println("为教师用户" + username + "生成teacherId: " + teacherId);
        return teacherId;
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
        String studentId = "S" + String.format("%06d", Integer.parseInt(numStr));
        System.out.println("为学生用户" + username + "生成studentId: " + studentId);
        return studentId;
    }
    
    /**
     * 添加测试用户 - 不再自动执行，由TestDataInitializer调用
     */
    public void initTestUsers() {
        System.out.println("初始化测试用户数据...");
        
        // 添加教师测试用户
        createTestUser("teacher1", "teacher123", "teacher", "张老师");
        createTestUser("teacher2", "teacher123", "teacher", "李老师");
        createTestUser("teacher3", "teacher123", "teacher", "王老师");

        // 添加学生测试用户
        createTestUser("student1", "student123", "student", "张三");
        createTestUser("student2", "student123", "student", "李四");
        createTestUser("student3", "student123", "student", "王五");
        createTestUser("student4", "student123", "student", "赵六");
        createTestUser("student5", "student123", "student", "钱七");

        // 添加管理员测试用户
        createTestUser("admin1", "admin123", "admin", "系统管理员");
        
        // 更新现有用户的teacher_id和student_id
        updateExistingUsersIds();
        
        System.out.println("测试用户数据初始化完成！");
    }
    
    /**
     * 更新现有用户的teacher_id和student_id字段
     */
    private void updateExistingUsersIds() {
        System.out.println("更新现有用户的特定ID字段...");
        List<User> users = userRepository.findAll();
        int updatedCount = 0;
        
        for (User user : users) {
            boolean updated = false;
            
            // 如果是教师，总是更新teacherId以确保格式正确
            if ("teacher".equals(user.getRole())) {
                String teacherId = generateTeacherId(user.getUsername());
                if (!teacherId.equals(user.getTeacherId())) {
                    user.setTeacherId(teacherId);
                    updated = true;
                    System.out.println("更新教师用户" + user.getUsername() + "的teacherId: " + teacherId);
                }
            }
            
            // 如果是学生，总是更新studentId以确保格式正确
            if ("student".equals(user.getRole())) {
                String studentId = generateStudentId(user.getUsername());
                if (!studentId.equals(user.getStudentId())) {
                    user.setStudentId(studentId);
                    updated = true;
                    System.out.println("更新学生用户" + user.getUsername() + "的studentId: " + studentId);
                }
            }
            
            if (updated) {
                userRepository.save(user);
                updatedCount++;
            }
        }
        
        System.out.println("用户ID更新完成，共更新了" + updatedCount + "个用户");
    }
    
    /**
     * 添加测试用户（备用方法）
     */
    public void addTestUsers() {
        System.out.println("添加测试用户和对应的教师、学生记录...");
        
        // 添加教师测试用户和对应的Teacher记录
        createTestUserWithDetails("teacher1", "teacher123", "teacher", "张老师");
        createTestUserWithDetails("teacher2", "teacher123", "teacher", "李老师");
        createTestUserWithDetails("teacher3", "teacher123", "teacher", "王老师");

        // 添加学生测试用户和对应的Student记录
        createTestUserWithDetails("student1", "student123", "student", "张三");
        createTestUserWithDetails("student2", "student123", "student", "李四");
        createTestUserWithDetails("student3", "student123", "student", "王五");
        createTestUserWithDetails("student4", "student123", "student", "赵六");
        createTestUserWithDetails("student5", "student123", "student", "钱七");

        // 添加管理员测试用户
        createTestUserWithDetails("admin1", "admin123", "admin", "系统管理员");
        
        System.out.println("测试用户添加完成！");
    }
    
    /**
     * 创建测试用户并添加关联的教师/学生记录
     */
    private void createTestUserWithDetails(String username, String password, String role, String name) {
        // 创建用户记录
        createTestUser(username, password, role, name);
        
        // 如果是教师角色，创建教师记录
        if ("teacher".equals(role)) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 检查教师记录是否已存在
                if (!teacherRepository.existsByTeacherId(user.getTeacherId())) {
                    Teacher teacher = new Teacher();
                    teacher.setTeacherId(user.getTeacherId());
                    teacher.setUsername(username);
                    teacher.setName(name);
                    teacherRepository.save(teacher);
                    System.out.println("创建教师记录成功: " + username);
                }
            }
        }
        // 如果是学生角色，创建学生记录
        else if ("student".equals(role)) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 检查学生记录是否已存在
                if (!studentRepository.existsByStudentId(user.getStudentId())) {
                    Student student = new Student();
                    student.setStudentId(user.getStudentId());
                    student.setUsername(username);
                    student.setName(name);
                    studentRepository.save(student);
                    System.out.println("创建学生记录成功: " + username);
                }
            }
        }
    }

    /**
     * 创建单个测试用户
     */
    public void createTestUser(String username, String password, String role, String name) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, passwordEncoder.encode(password), role, name);
            
            // 为教师和学生生成特定格式的ID
            if ("teacher".equals(role)) {
                user.setTeacherId(generateTeacherId(username));
            } else if ("student".equals(role)) {
                user.setStudentId(generateStudentId(username));
            }
            
            userRepository.save(user);
            System.out.println("创建测试用户成功: " + username + " (" + role + ")");
        } else {
            System.out.println("用户已存在，跳过创建: " + username);
        }
    }

    /**
     * 根据用户名查找用户
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据角色查找用户
     */
    public Iterable<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * 获取所有用户
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * 根据教师ID查找教师
     */
    public Optional<Teacher> findByTeacherId(String teacherId) {
        return teacherRepository.findByTeacherId(teacherId);
    }

    /**
     * 根据学生ID查找学生
     */
    public Optional<Student> findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }
    
    /**
     * 根据用户名查找教师
     */
    public Optional<Teacher> findTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }
    
    /**
     * 根据用户名查找学生
     */
    public Optional<Student> findStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    /**
     * 更新用户信息
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * 验证用户密码
     * 支持BCrypt加密密码验证和明文密码直接比较
     * 先检查密码是否为BCrypt格式，再决定使用哪种验证方式
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        // BCrypt密码通常以$2a$、$2b$或$2y$开头，长度固定为60个字符
        if (encodedPassword != null && encodedPassword.length() == 60 && encodedPassword.startsWith("$2")) {
            // 密码看起来是BCrypt格式，尝试使用BCrypt验证
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } else {
            // 密码不是BCrypt格式，直接进行明文比较
            System.out.println("检测到非BCrypt格式密码，使用明文比较");
            return rawPassword.equals(encodedPassword);
        }
    }
    
    /**
     * 加密密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 修改用户密码
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (validatePassword(oldPassword, user.getPassword())) {
                user.setPassword(encodePassword(newPassword));
                userRepository.save(user);
                
                // 同时更新对应角色表中的密码
                if ("teacher".equals(user.getRole()) && user.getTeacherId() != null) {
                    Optional<Teacher> teacherOpt = teacherRepository.findByTeacherId(user.getTeacherId());
                    teacherOpt.ifPresent(teacher -> {
                        teacher.setPassword(encodePassword(newPassword));
                        teacherRepository.save(teacher);
                    });
                } else if ("student".equals(user.getRole()) && user.getStudentId() != null) {
                    Optional<Student> studentOpt = studentRepository.findByStudentId(user.getStudentId());
                    studentOpt.ifPresent(student -> {
                        student.setPassword(encodePassword(newPassword));
                        studentRepository.save(student);
                    });
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * 保存教师记录
     */
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }
    
    /**
     * 保存学生记录
     */
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
}