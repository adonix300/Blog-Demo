package abdulgazizov.dev.blogdemo.services;

import abdulgazizov.dev.blogdemo.entities.UserEntity;

public interface UserService {
    UserEntity create(String username, String password);

    UserEntity getByUsername(String username);

    UserEntity getById(Long id);

    UserEntity getCurrent();
}
