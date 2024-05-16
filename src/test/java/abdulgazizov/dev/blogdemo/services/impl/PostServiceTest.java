package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.exceptions.ForbiddenException;
import abdulgazizov.dev.blogdemo.exceptions.PostNotFoundException;
import abdulgazizov.dev.blogdemo.mappers.PostMapper;
import abdulgazizov.dev.blogdemo.models.dto.PostDto;
import abdulgazizov.dev.blogdemo.models.entities.PostEntity;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.models.user.Role;
import abdulgazizov.dev.blogdemo.repositories.PostRepository;
import abdulgazizov.dev.blogdemo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private PostServiceImpl postService;
    private PostDto postDto;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .title("Post Title")
                .content("Post Content")
                .build();
        user = UserEntity.builder()
                .username("test_user")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    public void create_testSuccess() {
        //given
        PostEntity postEntity = PostEntity.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .user(user)
                .createdAt(postDto.getCreatedAt())
                .build();

        PostEntity savedPostEntity = PostEntity.builder()
                .id(1L)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .user(user)
                .createdAt(postDto.getCreatedAt())
                .build();

        when(userService.getCurrent()).thenReturn(user);
        when(postMapper.toEntity(postDto)).thenReturn(postEntity);
        when(postRepository.saveAndFlush(postEntity)).thenReturn(savedPostEntity);

        //when
        PostEntity createdPostEntity = postService.create(postDto);

        //then
        assertNotNull(createdPostEntity);
        assertEquals("Post Title", createdPostEntity.getTitle());
        assertEquals("Post Content", createdPostEntity.getContent());
        assertEquals(user, createdPostEntity.getUser());
        verify(postRepository).saveAndFlush(any(PostEntity.class));
    }

    @Test
    public void getAll_testSuccess() {
        //given
        List<PostEntity> list = List.of(
                PostEntity.builder()
                        .id(1L)
                        .title("Post Title 1")
                        .content("Post Content 1")
                        .build(),
                PostEntity.builder()
                        .id(2L)
                        .title("Post Title 2")
                        .content("Post Content 2")
                        .build()
        );

        when(postRepository.findAll()).thenReturn(list);

        //when
        List<PostEntity> allPostEntities = postService.getAll();

        //then
        assertEquals(list, allPostEntities);
        verify(postRepository).findAll();
    }

    @Test
    void getPostById_testSuccess() {
        // Given
        Long id = 1L;
        PostEntity post = PostEntity.builder()
                .id(id)
                .title("Post Title")
                .content("Post Content")
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        // When
        PostEntity postById = postService.getById(id);

        // Then
        assertNotNull(postById);
        assertEquals(id, postById.getId());
        assertEquals("Post Title", postById.getTitle());
        assertEquals("Post Content", postById.getContent());
        verify(postRepository).findById(id);
    }

    @Test
    void getPostById_testPostNotFound() {
        // Given
        Long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        // When
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> postService.getById(id));

        // Then
        assertEquals("Post with id 1 not found", exception.getMessage());
    }

    @Test
    void updatePost_testSuccess() {
        // Given
        Long id = 1L;

        PostEntity post = PostEntity.builder()
                .id(id)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();


        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(postRepository.saveAndFlush(any(PostEntity.class))).thenReturn(post);

        // When
        PostEntity updatedPost = postService.update(id, postDto);

        // Then
        assertNotNull(updatedPost);
        assertEquals(id, updatedPost.getId());
        assertEquals("Post Title", updatedPost.getTitle());
        assertEquals("Post Content", updatedPost.getContent());
        verify(postRepository).saveAndFlush(any(PostEntity.class));
    }

    @Test
    void testUpdatePost_Forbidden() {
        // Given
        Long id = 1L;

        PostEntity post = PostEntity.builder()
                .id(id)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("another_username");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        // When
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> postService.update(id, postDto));

        // Then
        assertEquals("User another_username have not enough rights to update this post", exception.getMessage());
    }

    @Test
    void delete_testSuccess() {
        //given
        Long id = 1L;

        PostEntity post = PostEntity.builder()
                .id(id)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getUsername());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        //when
        postService.delete(id);

        //then
        verify(postRepository).deleteById(id);
    }

    @Test
    void delete_testForbidden() {
        //given
        Long id = 1L;

        PostEntity post = PostEntity.builder()
                .id(id)
                .title("Post Title")
                .content("Post Content")
                .user(user)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("another_user");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        //when
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> postService.delete(id));

        //then
        assertEquals("User another_user have not enough rights to delete this post", exception.getMessage());
    }

    @Test
    void testDeletePost_PostNotFound() {
        // Given
        Long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        // When
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> postService.delete(id));

        // Then
        assertEquals("Post with id 1 not found", exception.getMessage());
    }
}