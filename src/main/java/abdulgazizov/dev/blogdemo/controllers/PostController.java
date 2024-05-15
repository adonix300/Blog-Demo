package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.mappers.PostMapper;
import abdulgazizov.dev.blogdemo.models.dto.PostDto;
import abdulgazizov.dev.blogdemo.services.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping()
    public ResponseEntity<PostDto> createPost(@RequestBody @Valid PostDto postDto) {
        PostDto savedPost = postMapper.toDto(postService.create(postDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAll() {
        List<PostDto> posts = postService.getAll().stream().map(postMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable @NotNull Long id) {
        PostDto post = postMapper.toDto(postService.getById(id));
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> update(@PathVariable @NotNull Long id, @RequestBody @Valid PostDto postDto) {
        PostDto updatedPost = postMapper.toDto(postService.update(id, postDto));
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable @NotNull Long id) {
        postService.delete(id);
        return ResponseEntity.ok("Successfully deleted post");
    }
}
