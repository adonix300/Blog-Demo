package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.models.dto.UserDto;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper extends Mapper<UserDto, UserEntity> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}
