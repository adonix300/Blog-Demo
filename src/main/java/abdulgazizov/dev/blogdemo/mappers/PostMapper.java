package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.dto.PostDto;
import abdulgazizov.dev.blogdemo.entities.PostEntity;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PostMapper extends Mapper<PostDto, PostEntity>{
}
