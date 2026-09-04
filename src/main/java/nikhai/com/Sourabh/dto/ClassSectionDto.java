package nikhai.com.Sourabh.dto;

import lombok.Data;

@Data
public class ClassSectionDto {
    private Long id;
    private String name;
    private Long departmentId;
    private String departmentName;
    private Integer semester;
    private String academicYear;
}
