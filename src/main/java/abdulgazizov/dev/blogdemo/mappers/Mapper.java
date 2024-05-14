package abdulgazizov.dev.blogdemo.mappers;

public interface Mapper <Dto, Entity> {
    Entity toEntity(Dto dto);
    Dto toDto(Entity entity);
}
