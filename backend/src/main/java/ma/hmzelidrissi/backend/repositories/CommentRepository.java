package ma.hmzelidrissi.backend.repositories;

import ma.hmzelidrissi.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}