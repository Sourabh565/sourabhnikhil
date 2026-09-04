package nikhai.com.Sourabh.dto;

import lombok.Data;
import nikhai.com.Sourabh.enums.SessionStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceSessionDto {
    private Long id;
    private Long subjectAssignmentId;
    private String subjectName;
    private String facultyName;
    private String sectionName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private SessionStatus sessionStatus;
}
