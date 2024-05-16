package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.exceptions.UserNotFoundException;
import abdulgazizov.dev.blogdemo.models.dto.AuthRequest;
import abdulgazizov.dev.blogdemo.models.dto.AuthResponse;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.models.user.CustomUserDetails;
import abdulgazizov.dev.blogdemo.secutiry.JwtService;
import abdulgazizov.dev.blogdemo.services.CustomUserDetailsService;
import abdulgazizov.dev.blogdemo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("test_user");
        authRequest.setPassword("password");

        user = UserEntity.builder()
                .id(1L)
                .username(authRequest.getUsername())
                .password(authRequest.getPassword())
                .build();
    }

    @Test
    public void signup_testSuccess() {
        // Given

        UserDetails userDetails = new CustomUserDetails(user);

        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(userService.create(eq(authRequest.getUsername()), anyString())).thenReturn(user);
        when(userDetailsService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt_token");

        // When
        AuthResponse authResponse = authService.signUp(authRequest);

        // Then
        assertNotNull(authResponse);
        assertEquals("jwt_token", authResponse.getToken());
        verify(userService).create(eq(authRequest.getUsername()), anyString());
        verify(userDetailsService).loadUserByUsername(authRequest.getUsername());
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void signup_testUsernameAlreadyExists() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        when(userService.create(eq(authRequest.getUsername()), anyString()))
                .thenThrow(new BadRequestException("User with username: " + authRequest.getUsername() + " already exists"));

        // When
        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.signUp(authRequest));

        // Then
        assertEquals("User with username: test_user already exists", exception.getMessage());
    }

    @Test
    public void signin_testSuccess() {
        // Given
        UserDetails userDetails = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken("test_user", "password"));
        when(userDetailsService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt_token");

        // When
        AuthResponse authResponse = authService.signIn(authRequest);

        // Then
        assertNotNull(authResponse);
        assertEquals("jwt_token", authResponse.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(authRequest.getUsername());
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void signin_testInvalidCredentials() {
        // Given

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid username or password"));

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.signIn(authRequest));

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid username or password", exception.getReason());
    }
}