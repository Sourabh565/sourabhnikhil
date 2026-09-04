package nikhai.com.Sourabh.dto;

import lombok.Data;

@Data
public class AttendanceSubmitRequest {
    private Long sessionId;
    private String remarks;
}
