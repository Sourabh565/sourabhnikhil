package nikhai.com.Sourabh.dto;

import lombok.Data;
import nikhai.com.Sourabh.enums.Role;
import nikhai.com.Sourabh.enums.Status;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private Status status;
}
