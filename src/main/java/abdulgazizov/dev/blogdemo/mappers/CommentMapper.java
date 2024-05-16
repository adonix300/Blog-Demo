package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.models.entities.CommentEntity;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface CommentMapper extends Mapper<CommentDto, CommentEntity> {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "post", ignore = true)
    CommentEntity toEntity(CommentDto commentDto);

    @Mapping(target = "user", source = "user")
    CommentDto toDto(CommentEntity commentEntity);
}
