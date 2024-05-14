package abdulgazizov.dev.blogdemo.repositories;

import abdulgazizov.dev.blogdemo.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
