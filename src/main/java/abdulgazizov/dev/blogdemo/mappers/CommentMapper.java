package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.dto.CommentDto;
import abdulgazizov.dev.blogdemo.entities.CommentEntity;

@org.mapstruct.Mapper(componentModel = "spring")
public interface CommentMapper extends Mapper<CommentDto, CommentEntity> {
}
