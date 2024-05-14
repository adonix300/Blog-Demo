package abdulgazizov.dev.blogdemo.services.impl;

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

    @Override
    public UserEntity create(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("User with username: " + username + " already exists");
        }

        final UserEntity entity = UserEntity.builder()
                .username(username)
                .password(password)
                .role(Role.ROLE_USER)
                .build();

        return userRepository.saveAndFlush(entity);
    }

    public UserEntity getByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User with username: " + username + " does not exist"));
    }

    public UserEntity getById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException("User with id: " + id + " does not exist"));
    }

    public UserEntity getCurrent() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
}
