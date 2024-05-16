package abdulgazizov.dev.blogdemo.services;

import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.models.entities.CommentEntity;

import java.util.List;

public interface CommentService {
    CommentEntity create(Long postId, CommentDto commentDto);

    List<CommentEntity> getCommentsByPostId(Long postId);

    void delete(Long postId, Long commentId);
}
