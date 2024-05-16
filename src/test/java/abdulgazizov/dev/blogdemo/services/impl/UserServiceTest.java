package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.exceptions.UserNotFoundException;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.models.user.Role;
import abdulgazizov.dev.blogdemo.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void create_testSuccess() {
        //given
        String username = "test_user";
        String password = "password";

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(password)
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(user);

        //when
        var response = userService.create(username, password);

        //then
        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals(password, response.getPassword());
        assertEquals(Role.ROLE_USER, response.getRole());
    }

    @Test
    void create_testUsernameAlreadyExists() {
        // Given
        String username = "test_user";
        String password = "password";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.create(username, password));

        // Then
        assertEquals("User with username: " + username + " already exists", exception.getMessage());
    }

    @Test
    void getByUsername_testSuccess() {
        // Given
        String username = "test_user";
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        // When
        UserEntity user = userService.getByUsername(username);

        // Then
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getByUsername_testUserNotFound() {
        // Given
        String username = "test_user";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getByUsername(username));

        // Then
        assertEquals("User with username: " + username + " does not exist", exception.getMessage());
    }

    @Test
    void getById_testSuccess() {
        // Given
        Long id = 1L;
        UserEntity userEntity = UserEntity.builder()
                .id(id)
                .username("test_user")
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

        // When
        UserEntity user = userService.getById(id);

        // Then
        assertNotNull(user);
        assertEquals(id, user.getId());
        verify(userRepository).findById(id);
    }

    @Test
    void getById_testUserNotFound() {
        // Given
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getById(id));

        // Then
        assertEquals("User with id: " + id + " does not exist", exception.getMessage());
    }

    @Test
    void getCurrentUser_testSuccess() {
        // Given
        String username = "test_user";
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        // When
        UserEntity user = userService.getCurrent();

        // Then
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        verify(userRepository).findByUsername(username);
    }
}