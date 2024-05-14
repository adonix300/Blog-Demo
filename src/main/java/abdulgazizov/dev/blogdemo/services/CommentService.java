package abdulgazizov.dev.blogdemo.services;

import abdulgazizov.dev.blogdemo.dto.CommentDto;
import abdulgazizov.dev.blogdemo.entities.CommentEntity;

import java.util.List;

public interface CommentService {
    CommentEntity create(Long postId, CommentDto commentDto);

    List<CommentEntity> getCommentsByPostId(Long postId);
}
