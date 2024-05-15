package abdulgazizov.dev.blogdemo.repositories;

import abdulgazizov.dev.blogdemo.models.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Optional<List<CommentEntity>> findAllByPost_Id(Long PostId);
}
