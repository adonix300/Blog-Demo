package abdulgazizov.dev.blogdemo.services;

import abdulgazizov.dev.blogdemo.models.dto.PostDto;
import abdulgazizov.dev.blogdemo.models.entities.PostEntity;

import java.util.List;

public interface PostService {
    PostEntity create(PostDto postDto);

    List<PostEntity> getAll();

    PostEntity getById(Long id);

    PostEntity update(Long id, PostDto postDto);

    void delete(Long id);
}
