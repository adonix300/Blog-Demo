package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.dto.AuthRequest;
import abdulgazizov.dev.blogdemo.dto.AuthResponse;
import abdulgazizov.dev.blogdemo.services.CustomUserDetailsService;
import abdulgazizov.dev.blogdemo.secutiry.JwtService;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse signUp(AuthRequest authRequest) {
        userService.create(authRequest.getUsername(), passwordEncoder.encode(authRequest.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken);
    }

    public AuthResponse signIn(AuthRequest authRequest) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.getUsername(),
                                authRequest.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken);
    }
}
