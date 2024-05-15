package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.models.user.Role;
import abdulgazizov.dev.blogdemo.repositories.UserRepository;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserEntity create(String username, String password) {
        log.info("Creating user with username: {}", username);
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("User with username: " + username + " already exists");
        }

        final UserEntity entity = UserEntity.builder()
                .username(username)
                .password(password)
                .role(Role.ROLE_USER)
                .build();

        UserEntity savedUser = userRepository.saveAndFlush(entity);
        log.info("User created successfully with username: {}", username);
        return savedUser;
    }

    public UserEntity getByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User with username: " + username + " does not exist"));
    }

    public UserEntity getById(Long id) {
        log.info("Fetching user by id: {}", id);
        return userRepository
                .findById(id)
                .orElseThrow(() -> new BadRequestException("User with id: " + id + " does not exist"));
    }

    public UserEntity getCurrent() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Fetching current user with username: {}", username);
        return getByUsername(username);
    }
}
