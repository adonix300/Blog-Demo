package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.exceptions.CommentNotFoundException;
import abdulgazizov.dev.blogdemo.exceptions.ForbiddenException;
import abdulgazizov.dev.blogdemo.mappers.CommentMapper;
import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.models.entities.CommentEntity;
import abdulgazizov.dev.blogdemo.models.entities.PostEntity;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.models.user.Role;
import abdulgazizov.dev.blogdemo.repositories.CommentRepository;
import abdulgazizov.dev.blogdemo.services.PostService;
import abdulgazizov.dev.blogdemo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private PostService postService;
    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentDto commentDto;
    private UserEntity user;
    private PostEntity post;
    private Long postId;

    @BeforeEach
    public void setUp() {
        commentDto = CommentDto.builder()
                .content("Comment Content")
                .build();

        user = UserEntity.builder()
                .id(1L)
                .username("test_user")
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        postId = 1L;

        post = PostEntity.builder()
                .id(postId)
                .title("Post Title")
                .content("Post Content")
                .build();
    }

    @Test
    public void createComment_testSuccess() {
        // Given

        CommentEntity commentEntity = CommentEntity.builder()
                .content("Comment Content")
                .post(post)
                .user(user)
                .build();

        CommentEntity savedCommentEntity = CommentEntity.builder()
                .id(1L)
                .content("Comment Content")
                .post(post)
                .user(user)
                .build();

        when(userService.getCurrent()).thenReturn(user);
        when(postService.getById(postId)).thenReturn(post);
        when(commentMapper.toEntity(commentDto)).thenReturn(commentEntity);

        when(commentRepository.saveAndFlush(commentEntity)).thenReturn(savedCommentEntity);
        // When
        CommentEntity createdComment = commentService.create(postId, commentDto);

        // Then
        assertNotNull(createdComment);
        assertEquals(user, createdComment.getUser());
        assertEquals(post, createdComment.getPost());
        assertEquals("Comment Content", createdComment.getContent());
        verify(commentRepository).saveAndFlush(any(CommentEntity.class));
    }

    @Test
    public void createComment_testNullCommentDto() {
        // Given
        CommentDto commentDto = null;

        // When
        BadRequestException exception = assertThrows(BadRequestException.class, () -> commentService.create(postId, commentDto));

        // Then
        assertEquals("Comment cannot be null", exception.getMessage());
    }


    @Test
    public void getCommentsByPostId_testSuccess() {
        // Given
        List<CommentEntity> comments = List.of(
                CommentEntity.builder()
                        .id(1L)
                        .content("Comment 1")
                        .build(),
                CommentEntity.builder()
                        .id(2L)
                        .content("Comment 2")
                        .build()
        );

        when(commentRepository.findAllByPost_Id(postId)).thenReturn(Optional.of(comments));

        // When
        List<CommentEntity> commentsByPostId = commentService.getCommentsByPostId(postId);

        // Then
        assertEquals(comments, commentsByPostId);
        verify(commentRepository).findAllByPost_Id(postId);
    }

    @Test
    public void getCommentsByPostId_testNullPostId() {
        // Given
        Long postId = null;

        // When
        BadRequestException exception = assertThrows(BadRequestException.class, () -> commentService.getCommentsByPostId(postId));

        // Then
        assertEquals("Post ID cannot be null", exception.getMessage());
    }



    @Test
    public void deleteComment_testSuccess() {
        // Given
        Long commentId = 1L;
        CommentEntity commentEntity = CommentEntity.builder()
                .id(commentId)
                .content("Comment Content")
                .post(post)
                .user(user)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));
        when(userService.getCurrent()).thenReturn(user);
        // When
        commentService.delete(postId, commentId);

        // Then
        verify(commentRepository).delete(commentEntity);
    }

    @Test
    public void deleteComment_testForbidden() {
        // Given
        Long commentId = 1L;
        CommentEntity commentEntity = CommentEntity.builder()
                .id(commentId)
                .content("Comment Content")
                .post(post)
                .user(user)
                .build();

        UserEntity anotherUser = UserEntity.builder()
                .id(2L)
                .username("another_user")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));
        when(userService.getCurrent()).thenReturn(anotherUser);

        // When
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> commentService.delete(postId, commentId));

        // Then
        assertEquals("You are not allowed to delete this comment", exception.getMessage());
    }

    @Test
    public void deleteComment_testBadRequest() {
        // Given
        Long commentId = 1L;
        CommentEntity commentEntity = CommentEntity.builder()
                .id(commentId)
                .content("Comment Content")
                .post(post)
                .user(user)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));
        when(userService.getCurrent()).thenReturn(user);
        // When
        BadRequestException exception = assertThrows(BadRequestException.class, () -> commentService.delete(2L, commentId));

        // Then
        assertEquals("Bad request", exception.getMessage());
    }

    @Test
    public void deleteComment_testCommentNotFound() {
        // Given
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When
        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> commentService.delete(postId, commentId));

        // Then
        assertEquals("Comment not found", exception.getMessage());
    }

    @Test
    public void deleteComment_testNullPostId() {
        // Given
        Long postId = null;
        Long commentId = 1L;

        CommentEntity commentEntity = CommentEntity.builder()
                .id(commentId)
                .content("Comment Content")
                .post(post)
                .user(user)
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentEntity));
        when(userService.getCurrent()).thenReturn(user);

        // When
        BadRequestException exception = assertThrows(BadRequestException.class, () -> commentService.delete(postId, commentId));

        // Then
        assertEquals("Bad request", exception.getMessage());
    }

    @Test
    public void deleteComment_testNullCommentId() {
        // Given
        Long postId = 1L;
        Long commentId = null;

        // When
        BadRequestException exception = assertThrows(BadRequestException.class, () -> commentService.delete(postId, commentId));

        // Then
        assertEquals("Comment ID cannot be null", exception.getMessage());
    }
}