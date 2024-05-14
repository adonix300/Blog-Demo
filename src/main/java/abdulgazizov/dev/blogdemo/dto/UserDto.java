package abdulgazizov.dev.blogdemo.dto;

import abdulgazizov.dev.blogdemo.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private List<PostDto> posts;
    private List<CommentDto> comments;
}
