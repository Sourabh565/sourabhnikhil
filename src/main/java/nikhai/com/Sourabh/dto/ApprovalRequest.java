package nikhai.com.Sourabh.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalRequest {
    @NotNull(message = "Attendance ID is required")
    private Long attendanceId;
    
    private String rejectionReason;
}
