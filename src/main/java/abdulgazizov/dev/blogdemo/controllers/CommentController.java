package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.mappers.CommentMapper;
import abdulgazizov.dev.blogdemo.services.CommentService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        List<CommentDto> list = commentService.getCommentsByPostId(postId).stream().map(commentMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        CommentDto comment = commentMapper.toDto(commentService.create(postId, commentDto));
        return ResponseEntity.ok(comment);
    }
}
