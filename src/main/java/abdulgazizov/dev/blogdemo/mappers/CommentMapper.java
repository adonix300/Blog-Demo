package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.models.entities.CommentEntity;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface CommentMapper extends Mapper<CommentDto, CommentEntity> {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
}
