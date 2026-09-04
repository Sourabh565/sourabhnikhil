package nikhai.com.Sourabh.dto;

import lombok.Data;

@Data
public class StudentDto {
    private Long id;
    private String enrollmentNo;
    private String firstName;
    private String lastName;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private Integer semester;
    private String section;
    private Integer admissionYear;
    private Long userId;
    private String username;
}
