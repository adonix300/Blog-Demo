package abdulgazizov.dev.blogdemo.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    private UserDto author;
    @Builder.Default
    @JsonProperty("created_at")
    private Instant createdAt = Instant.now();
    @Builder.Default
    private List<CommentDto> comments = new ArrayList<>();
}