package abdulgazizov.dev.blogdemo.repositories;

import abdulgazizov.dev.blogdemo.models.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Optional<PostEntity> findByTitle(String title);
    Optional<List<PostEntity>> findAllByUser_Id(Long id);
}
