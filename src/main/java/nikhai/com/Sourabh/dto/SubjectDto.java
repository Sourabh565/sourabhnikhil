package nikhai.com.Sourabh.dto;

import lombok.Data;

@Data
public class SubjectDto {
    private Long id;
    private String code;
    private String name;
    private Long departmentId;
    private String departmentName;
    private Integer semester;
    private Integer credits;
}
