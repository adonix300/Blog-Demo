package abdulgazizov.dev.blogdemo.repositories;

import abdulgazizov.dev.blogdemo.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
