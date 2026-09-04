package nikhai.com.Sourabh.dto;

import lombok.Data;
import nikhai.com.Sourabh.enums.AttendanceStatus;

import java.time.LocalDateTime;

@Data
public class AttendanceDto {
    private Long id;
    private Long sessionId;
    private String subjectName;
    private Long studentId;
    private String studentName;
    private AttendanceStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private String rejectionReason;
    private String remarks;
    private Boolean hasSelfie;
}
