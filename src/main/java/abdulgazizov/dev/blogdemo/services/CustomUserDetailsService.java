package abdulgazizov.dev.blogdemo.services;

import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.models.user.CustomUserDetails;
import abdulgazizov.dev.blogdemo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User with username: " + username + " does not exist"));

        return new CustomUserDetails(entity);
    }
}
