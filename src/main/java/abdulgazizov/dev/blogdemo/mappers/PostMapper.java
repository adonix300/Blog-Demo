package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.models.dto.PostDto;
import abdulgazizov.dev.blogdemo.models.entities.PostEntity;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PostMapper extends Mapper<PostDto, PostEntity> {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "author", source = "user")
    @Mapping(target = "comments", source = "comments")
    PostDto toDto(PostEntity postEntity);

    @Mapping(target = "user", source = "author")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "comments[].user", ignore = true)
    @Mapping(target = "comments[].post", ignore = true)
    PostEntity toEntity(PostDto postDto);
}
