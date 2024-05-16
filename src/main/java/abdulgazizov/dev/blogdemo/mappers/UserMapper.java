package abdulgazizov.dev.blogdemo.mappers;

import abdulgazizov.dev.blogdemo.models.dto.UserDto;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper extends Mapper<UserDto, UserEntity> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(UserEntity userEntity);

    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserDto userDto);
}
