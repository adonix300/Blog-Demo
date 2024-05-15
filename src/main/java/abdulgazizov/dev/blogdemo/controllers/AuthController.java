package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.models.dto.AuthRequest;
import abdulgazizov.dev.blogdemo.models.dto.AuthResponse;
import abdulgazizov.dev.blogdemo.services.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(authRequest));
    }

    @PostMapping("signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.ok(authService.signIn(authRequest));
    }
}
