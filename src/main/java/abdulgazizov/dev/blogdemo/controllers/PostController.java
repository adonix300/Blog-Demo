package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.models.dto.PostDto;
import abdulgazizov.dev.blogdemo.mappers.PostMapper;
import abdulgazizov.dev.blogdemo.services.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostController {
    private final PostServiceImpl postService;
    private final PostMapper postMapper;

    @PostMapping()
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        PostDto savedPost = postMapper.toDto(postService.create(postDto));
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAll() {
        List<PostDto> posts = postService.getAll().stream().map(postMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable Long id) {
        PostDto post = postMapper.toDto(postService.getById(id));
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> update(@PathVariable Long id, @RequestBody PostDto postDto) {
        PostDto updatedPost = postMapper.toDto(postService.update(id, postDto));
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok("Successfully deleted post");
    }
}
