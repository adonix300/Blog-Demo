package abdulgazizov.dev.blogdemo.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    @Builder.Default
    @JsonProperty("created_at")
    private Instant createdAt = Instant.now();
    private UserDto user;
}
