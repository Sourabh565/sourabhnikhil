package nikhai.com.Sourabh.service;

import nikhai.com.Sourabh.dto.*;
import nikhai.com.Sourabh.entity.*;
import nikhai.com.Sourabh.enums.AttendanceStatus;
import nikhai.com.Sourabh.enums.SessionStatus;
import nikhai.com.Sourabh.enums.Status;
import nikhai.com.Sourabh.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final ClassSectionRepository classSectionRepository;
    private final SubjectAssignmentRepository subjectAssignmentRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final AuditLogRepository auditLogRepository;
    
    public AdminService(UserRepository userRepository, StudentRepository studentRepository,
                        DepartmentRepository departmentRepository, SubjectRepository subjectRepository,
                        ClassSectionRepository classSectionRepository, SubjectAssignmentRepository subjectAssignmentRepository,
                        AttendanceSessionRepository attendanceSessionRepository, AttendanceRepository attendanceRepository,
                        AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.subjectRepository = subjectRepository;
        this.classSectionRepository = classSectionRepository;
        this.subjectAssignmentRepository = subjectAssignmentRepository;
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.attendanceRepository = attendanceRepository;
        this.auditLogRepository = auditLogRepository;
    }
    
    // Department CRUD
    public DepartmentDto createDepartment(DepartmentDto dto) {
        if (departmentRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Department code already exists");
        }
        Department department = new Department();
        department.setCode(dto.getCode());
        department.setName(dto.getName());
        department = departmentRepository.save(department);
        return mapToDepartmentDto(department);
    }
    
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDepartmentDto)
                .collect(Collectors.toList());
    }
    
    // Student CRUD
    public StudentDto createStudent(StudentDto dto, String password) {
        if (studentRepository.existsByEnrollmentNo(dto.getEnrollmentNo())) {
            throw new RuntimeException("Enrollment number already exists");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(password); // Should be encoded in AuthService
        user.setRole(nikhai.com.Sourabh.enums.Role.USER);
        user.setStatus(Status.ACTIVE);
        user = userRepository.save(user);
        
        Student student = new Student();
        student.setUser(user);
        student.setEnrollmentNo(dto.getEnrollmentNo());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setPhone(dto.getPhone());
        if (dto.getDepartmentId() != null) {
            student.setDepartment(departmentRepository.findById(dto.getDepartmentId()).orElse(null));
        }
        student.setSemester(dto.getSemester());
        student.setSection(dto.getSection());
        student.setAdmissionYear(dto.getAdmissionYear());
        student = studentRepository.save(student);
        
        return mapToStudentDto(student);
    }
    
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToStudentDto)
                .collect(Collectors.toList());
    }
    
    // Subject CRUD
    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Subject code already exists");
        }
        Subject subject = new Subject();
        subject.setCode(dto.getCode());
        subject.setName(dto.getName());
        if (dto.getDepartmentId() != null) {
            subject.setDepartment(departmentRepository.findById(dto.getDepartmentId()).orElse(null));
        }
        subject.setSemester(dto.getSemester());
        subject.setCredits(dto.getCredits());
        subject = subjectRepository.save(subject);
        return mapToSubjectDto(subject);
    }
    
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToSubjectDto)
                .collect(Collectors.toList());
    }
    
    // Class Section CRUD
    public ClassSectionDto createClassSection(ClassSectionDto dto) {
        ClassSection section = new ClassSection();
        section.setName(dto.getName());
        if (dto.getDepartmentId() != null) {
            section.setDepartment(departmentRepository.findById(dto.getDepartmentId()).orElse(null));
        }
        section.setSemester(dto.getSemester());
        section.setAcademicYear(dto.getAcademicYear());
        section = classSectionRepository.save(section);
        return mapToClassSectionDto(section);
    }
    
    public List<ClassSectionDto> getAllClassSections() {
        return classSectionRepository.findAll().stream()
                .map(this::mapToClassSectionDto)
                .collect(Collectors.toList());
    }
    
    // Attendance Session
    public AttendanceSessionDto createAttendanceSession(AttendanceSessionDto dto, Long createdByUserId) {
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        SubjectAssignment assignment = subjectAssignmentRepository.findById(dto.getSubjectAssignmentId())
                .orElseThrow(() -> new RuntimeException("Subject assignment not found"));
        
        AttendanceSession session = new AttendanceSession();
        session.setSubjectAssignment(assignment);
        session.setDate(dto.getDate());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setSessionStatus(SessionStatus.SCHEDULED);
        session.setCreatedBy(createdBy);
        session = attendanceSessionRepository.save(session);
        
        return mapToAttendanceSessionDto(session);
    }
    
    public List<AttendanceSessionDto> getAllAttendanceSessions() {
        return attendanceSessionRepository.findAll().stream()
                .map(this::mapToAttendanceSessionDto)
                .collect(Collectors.toList());
    }
    
    // Approval
    @Transactional
    public void approveAttendance(Long attendanceId, Long approvedByUserId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        User approvedBy = userRepository.findById(approvedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        attendance.setStatus(AttendanceStatus.APPROVED);
        attendance.setApprovedAt(LocalDateTime.now());
        attendance.setApprovedBy(approvedBy);
        attendanceRepository.save(attendance);
        
        // Create audit log
        createAuditLog(approvedBy, "APPROVE", "Attendance", attendanceId, 
                attendance.getStatus().name(), AttendanceStatus.APPROVED.name());
    }
    
    @Transactional
    public void rejectAttendance(Long attendanceId, String reason, Long approvedByUserId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        User approvedBy = userRepository.findById(approvedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        attendance.setStatus(AttendanceStatus.REJECTED);
        attendance.setRejectionReason(reason);
        attendance.setApprovedAt(LocalDateTime.now());
        attendance.setApprovedBy(approvedBy);
        attendanceRepository.save(attendance);
        
        // Create audit log
        createAuditLog(approvedBy, "REJECT", "Attendance", attendanceId, 
                attendance.getStatus().name(), AttendanceStatus.REJECTED.name());
    }
    
    public List<AttendanceDto> getPendingAttendances() {
        return attendanceRepository.findByStatus(AttendanceStatus.SUBMITTED).stream()
                .map(this::mapToAttendanceDto)
                .collect(Collectors.toList());
    }
    
    private void createAuditLog(User actor, String action, String entityType, Long entityId, 
                                String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setActorUser(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValueSummary(oldValue);
        log.setNewValueSummary(newValue);
        auditLogRepository.save(log);
    }
    
    // Mapping methods
    private DepartmentDto mapToDepartmentDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setCode(department.getCode());
        dto.setName(department.getName());
        return dto;
    }
    
    private StudentDto mapToStudentDto(Student student) {
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setEnrollmentNo(student.getEnrollmentNo());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setPhone(student.getPhone());
        dto.setSemester(student.getSemester());
        dto.setSection(student.getSection());
        dto.setAdmissionYear(student.getAdmissionYear());
        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
            dto.setDepartmentName(student.getDepartment().getName());
        }
        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
            dto.setUsername(student.getUser().getUsername());
        }
        return dto;
    }
    
    private SubjectDto mapToSubjectDto(Subject subject) {
        SubjectDto dto = new SubjectDto();
        dto.setId(subject.getId());
        dto.setCode(subject.getCode());
        dto.setName(subject.getName());
        dto.setSemester(subject.getSemester());
        dto.setCredits(subject.getCredits());
        if (subject.getDepartment() != null) {
            dto.setDepartmentId(subject.getDepartment().getId());
            dto.setDepartmentName(subject.getDepartment().getName());
        }
        return dto;
    }
    
    private ClassSectionDto mapToClassSectionDto(ClassSection section) {
        ClassSectionDto dto = new ClassSectionDto();
        dto.setId(section.getId());
        dto.setName(section.getName());
        dto.setSemester(section.getSemester());
        dto.setAcademicYear(section.getAcademicYear());
        if (section.getDepartment() != null) {
            dto.setDepartmentId(section.getDepartment().getId());
            dto.setDepartmentName(section.getDepartment().getName());
        }
        return dto;
    }
    
    private AttendanceSessionDto mapToAttendanceSessionDto(AttendanceSession session) {
        AttendanceSessionDto dto = new AttendanceSessionDto();
        dto.setId(session.getId());
        dto.setDate(session.getDate());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setSessionStatus(session.getSessionStatus());
        if (session.getSubjectAssignment() != null) {
            dto.setSubjectAssignmentId(session.getSubjectAssignment().getId());
            if (session.getSubjectAssignment().getSubject() != null) {
                dto.setSubjectName(session.getSubjectAssignment().getSubject().getName());
            }
            if (session.getSubjectAssignment().getFaculty() != null) {
                dto.setFacultyName(session.getSubjectAssignment().getFaculty().getName());
            }
            if (session.getSubjectAssignment().getSection() != null) {
                dto.setSectionName(session.getSubjectAssignment().getSection().getName());
            }
        }
        return dto;
    }
    
    private AttendanceDto mapToAttendanceDto(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setStatus(attendance.getStatus());
        dto.setSubmittedAt(attendance.getSubmittedAt());
        dto.setApprovedAt(attendance.getApprovedAt());
        dto.setRejectionReason(attendance.getRejectionReason());
        dto.setRemarks(attendance.getRemarks());
        dto.setHasSelfie(attendance.getSelfie() != null);
        if (attendance.getSession() != null) {
            dto.setSessionId(attendance.getSession().getId());
            if (attendance.getSession().getSubjectAssignment() != null &&
                attendance.getSession().getSubjectAssignment().getSubject() != null) {
                dto.setSubjectName(attendance.getSession().getSubjectAssignment().getSubject().getName());
            }
        }
        if (attendance.getStudent() != null) {
            dto.setStudentId(attendance.getStudent().getId());
            dto.setStudentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName());
        }
        if (attendance.getApprovedBy() != null) {
            dto.setApprovedBy(attendance.getApprovedBy().getUsername());
        }
        return dto;
    }
}
