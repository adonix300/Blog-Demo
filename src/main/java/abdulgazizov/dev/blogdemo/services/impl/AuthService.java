package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.models.dto.AuthRequest;
import abdulgazizov.dev.blogdemo.models.dto.AuthResponse;
import abdulgazizov.dev.blogdemo.secutiry.JwtService;
import abdulgazizov.dev.blogdemo.services.CustomUserDetailsService;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse signUp(AuthRequest authRequest) {
        log.info("Attempting to sign up user: {}", authRequest.getUsername());
        userService.create(authRequest.getUsername(), passwordEncoder.encode(authRequest.getPassword()));
        log.info("User signed up successfully: {}", authRequest.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        log.info("JWT token generated for user: {}", authRequest.getUsername());
        return new AuthResponse(jwtToken);
    }

    public AuthResponse signIn(AuthRequest authRequest) {
        log.info("Attempting to sign in user: {}", authRequest.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()));
        log.info("User authenticated successfully: {}", authRequest.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        log.info("JWT token generated for user: {}", authRequest.getUsername());
        return new AuthResponse(jwtToken);
    }
}
