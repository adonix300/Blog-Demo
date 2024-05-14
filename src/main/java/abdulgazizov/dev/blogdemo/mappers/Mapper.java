package abdulgazizov.dev.blogdemo.mappers;

@org.mapstruct.Mapper
public interface Mapper <Dto, Entity> {
    Entity toEntity(Dto dto);
    Dto toDto(Entity entity);
}
