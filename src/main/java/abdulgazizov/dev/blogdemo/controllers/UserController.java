package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.dto.UserDto;
import abdulgazizov.dev.blogdemo.mappers.UserMapper;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("profile")
    public ResponseEntity<UserDto> profile() {
        UserDto userDto = userMapper.toDto(userService.getCurrent());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("profile/{id}")
    public ResponseEntity<UserDto> profile(@PathVariable Long id) {
        UserDto userDto = userMapper.toDto(userService.getById(id));
        return ResponseEntity.ok(userDto);
    }
}
