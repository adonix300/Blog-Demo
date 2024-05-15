package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.mappers.CommentMapper;
import abdulgazizov.dev.blogdemo.services.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable @NotNull Long postId) {
        List<CommentDto> list = commentService.getCommentsByPostId(postId).stream().map(commentMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable @NotNull Long postId, @RequestBody @Valid CommentDto commentDto) {
        CommentDto comment = commentMapper.toDto(commentService.create(postId, commentDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}
