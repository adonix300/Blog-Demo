package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.mappers.CommentMapper;
import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.models.entities.CommentEntity;
import abdulgazizov.dev.blogdemo.models.entities.PostEntity;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.repositories.CommentRepository;
import abdulgazizov.dev.blogdemo.services.CommentService;
import abdulgazizov.dev.blogdemo.services.PostService;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final PostService postService;

    @Transactional
    public CommentEntity create(Long postId, CommentDto commentDto) {
        log.info("Creating comment for post: {}", postId);

        UserEntity user = userService.getCurrent();
        PostEntity post = postService.getById(postId);
        CommentEntity commentEntity = commentMapper.toEntity(commentDto);
        commentEntity.setUser(user);
        commentEntity.setPost(post);
        CommentEntity savedComment = commentRepository.saveAndFlush(commentEntity);
        log.info("Comment created successfully for post: {}", postId);
        return savedComment;


    }

    public List<CommentEntity> getCommentsByPostId(Long postId) {
        log.info("Fetching comments for post: {}", postId);
        return commentRepository.findAllByPost_Id(postId).orElse(new ArrayList<CommentEntity>());
    }
}
