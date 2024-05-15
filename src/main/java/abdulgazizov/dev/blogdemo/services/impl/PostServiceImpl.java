package abdulgazizov.dev.blogdemo.services.impl;

import abdulgazizov.dev.blogdemo.exceptions.BadRequestException;
import abdulgazizov.dev.blogdemo.exceptions.PostNotFoundException;
import abdulgazizov.dev.blogdemo.mappers.PostMapper;
import abdulgazizov.dev.blogdemo.models.dto.PostDto;
import abdulgazizov.dev.blogdemo.models.entities.PostEntity;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.repositories.PostRepository;
import abdulgazizov.dev.blogdemo.services.PostService;
import abdulgazizov.dev.blogdemo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;

    @CacheEvict(value = "posts", allEntries = true)
    public PostEntity create(PostDto postDto) {
        log.info("Creating post");
        UserEntity user = userService.getCurrent();
        PostEntity postEntity = postMapper.toEntity(postDto);

        postEntity.setUser(user);
        PostEntity savedPost = postRepository.saveAndFlush(postEntity);

        log.info("Post created successfully");
        return savedPost;
    }

    @Cacheable("posts")
    public List<PostEntity> getAll() {
        log.info("Fetching all posts");
        return postRepository.findAll();
    }

    @Cacheable(value = "posts", key = "#id")
    public PostEntity getById(Long id) {
        log.info("Fetching post by id: {}", id);
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post with id " + id + " not found"));
    }

    @CacheEvict(value = "posts", allEntries = true)
    public PostEntity update(Long id, PostDto postDto) {
        log.info("Updating post with id: {}", id);
        PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post with id " + id + " not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!postEntity.getUser().getUsername().equals(username)) {
            throw new BadRequestException("User " + username + " not authorized to update this post");
        }
        postEntity.setTitle(postDto.getTitle());
        postEntity.setContent(postDto.getContent());
        PostEntity updatedPost = postRepository.saveAndFlush(postEntity);
        log.info("Post updated successfully with id: {}", id);
        return updatedPost;
    }

    @CacheEvict(value = "posts", allEntries = true)
    public void delete(Long id) {
        log.info("Deleting post with id: {}", id);
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            log.info("Post deleted successfully with id: {}", id);
        } else {
            throw new PostNotFoundException("Post with id " + id + " not found");
        }
    }
}
