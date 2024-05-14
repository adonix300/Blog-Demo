package abdulgazizov.dev.blogdemo.services;

import abdulgazizov.dev.blogdemo.dto.UserDto;

public interface UserService {
    UserDto create(String username, String password);
    UserDto getByUsername(String username);
    UserDto getById(Long id);
    UserDto getCurrent();
}
