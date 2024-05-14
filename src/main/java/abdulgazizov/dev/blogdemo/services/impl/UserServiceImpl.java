package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.dto.UserDto;
import abdulgazizov.dev.blogdemo.entities.UserEntity;
import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.mappers.UserMapper;
import abdulgazizov.dev.blogdemo.models.Role;
import abdulgazizov.dev.blogdemo.repositories.UserRepository;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("User with username: " + username + " already exists");
        }

        final UserEntity entity = UserEntity.builder()
                .username(username)
                .password(password)
                .role(Role.ROLE_USER)
                .build();

        final UserEntity savedUser = userRepository.saveAndFlush(entity);

        return userMapper.toDto(savedUser);
    }

    public UserDto getByUsername(String username) {
        final UserEntity entity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User with username: " + username + " does not exist"));

        return userMapper.toDto(entity);
    }

    public UserDto getById(Long id) {
        final UserEntity entity = userRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException("User with id: " + id + " does not exist"));

        return userMapper.toDto(entity);
    }

    public UserDto getCurrent() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

}
