package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.dto.PostDto;
import abdulgazizov.dev.blogdemo.entities.PostEntity;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PostMapper extends Mapper<PostDto, PostEntity> {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "author", source = "user")
    PostDto toDto(PostEntity postEntity);

    @Mapping(target = "user", source = "author")
    PostEntity toEntity(PostDto postDto);
}
