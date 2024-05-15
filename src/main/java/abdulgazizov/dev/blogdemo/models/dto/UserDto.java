package abdulgazizov.dev.blogdemo.models.dto;

import abdulgazizov.dev.blogdemo.models.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private Role role;
}
