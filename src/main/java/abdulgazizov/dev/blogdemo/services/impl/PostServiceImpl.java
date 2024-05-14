package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.dto.PostDto;
import abdulgazizov.dev.blogdemo.entities.PostEntity;
import abdulgazizov.dev.blogdemo.entities.UserEntity;
import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.mappers.PostMapper;
import abdulgazizov.dev.blogdemo.repositories.PostRepository;
import abdulgazizov.dev.blogdemo.services.PostService;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;

    public PostEntity create(PostDto postDto) {
        UserEntity user = userService.getCurrent();

        PostEntity postEntity = postMapper.toEntity(postDto);

        postEntity.setUser(user);

        return postRepository.saveAndFlush(postEntity);
    }

    public List<PostEntity> getAll() {
        return postRepository.findAll();
    }

    public PostEntity getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new BadRequestException("Post with id " + id + " not found"));
    }

    public PostEntity update(Long id, PostDto postDto) {
        PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new BadRequestException("Post with id " + id + " not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!postEntity.getUser().getUsername().equals(username)) {
            throw new BadRequestException("User " + username + " not authorized to update this post");
        }
        postEntity.setTitle(postDto.getTitle());
        postEntity.setContent(postDto.getContent());
        return postRepository.saveAndFlush(postEntity);

    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}
