package nikhai.com.Sourabh.service;

import nikhai.com.Sourabh.dto.AttendanceDto;
import nikhai.com.Sourabh.dto.AttendanceSessionDto;
import nikhai.com.Sourabh.dto.AttendanceSubmitRequest;
import nikhai.com.Sourabh.entity.Attendance;
import nikhai.com.Sourabh.entity.AttendanceSession;
import nikhai.com.Sourabh.entity.Student;
import nikhai.com.Sourabh.entity.User;
import nikhai.com.Sourabh.enums.AttendanceStatus;
import nikhai.com.Sourabh.repository.AttendanceRepository;
import nikhai.com.Sourabh.repository.AttendanceSessionRepository;
import nikhai.com.Sourabh.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceRepository attendanceRepository;
    
    public StudentService(StudentRepository studentRepository,
                          AttendanceSessionRepository attendanceSessionRepository,
                          AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceSessionRepository = attendanceSessionRepository;
        this.attendanceRepository = attendanceRepository;
    }
    
    public List<AttendanceSessionDto> getActiveSessions(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // For now, return all sessions. In production, filter by student's section/subject
        return attendanceSessionRepository.findAll().stream()
                .map(this::mapToAttendanceSessionDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AttendanceDto submitAttendance(Long userId, AttendanceSubmitRequest request) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        AttendanceSession session = attendanceSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Attendance session not found"));
        
        // Check if already submitted
        if (attendanceRepository.findBySessionAndStudent(session, student).isPresent()) {
            throw new RuntimeException("Attendance already submitted for this session");
        }
        
        Attendance attendance = new Attendance();
        attendance.setSession(session);
        attendance.setStudent(student);
        attendance.setStatus(AttendanceStatus.SUBMITTED);
        attendance.setSubmittedAt(LocalDateTime.now());
        attendance.setRemarks(request.getRemarks());
        attendance = attendanceRepository.save(attendance);
        
        return mapToAttendanceDto(attendance);
    }
    
    public List<AttendanceDto> getAttendanceHistory(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        return attendanceRepository.findByStudent(student).stream()
                .map(this::mapToAttendanceDto)
                .collect(Collectors.toList());
    }
    
    public AttendanceDto getAttendanceSummary(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        List<Attendance> attendances = attendanceRepository.findByStudent(student);
        
        long total = attendances.size();
        long approved = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.APPROVED)
                .count();
        long rejected = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.REJECTED)
                .count();
        long pending = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.SUBMITTED)
                .count();
        
        AttendanceDto summary = new AttendanceDto();
        summary.setStudentId(student.getId());
        summary.setStudentName(student.getFirstName() + " " + student.getLastName());
        summary.setRemarks(String.format("Total: %d, Approved: %d, Rejected: %d, Pending: %d", 
                total, approved, rejected, pending));
        
        return summary;
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
