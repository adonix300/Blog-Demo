package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.dto.CommentDto;
import abdulgazizov.dev.blogdemo.entities.CommentEntity;
import abdulgazizov.dev.blogdemo.entities.PostEntity;
import abdulgazizov.dev.blogdemo.entities.UserEntity;
import abdulgazizov.dev.blogdemo.mappers.CommentMapper;
import abdulgazizov.dev.blogdemo.repositories.CommentRepository;
import abdulgazizov.dev.blogdemo.services.CommentService;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final PostServiceImpl postService;

    public CommentEntity create(Long postId, CommentDto commentDto) {
        UserEntity user = userService.getCurrent();
        PostEntity post = postService.getById(postId);
        CommentEntity commentEntity = commentMapper.toEntity(commentDto);
        commentEntity.setUser(user);
        commentEntity.setPost(post);
        return commentRepository.saveAndFlush(commentEntity);
    }

    public List<CommentEntity> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPost_Id(postId).orElse(new ArrayList<CommentEntity>());
    }
}
