package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.dto.UserDto;
import abdulgazizov.dev.blogdemo.entities.UserEntity;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper extends Mapper<UserDto, UserEntity> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}
