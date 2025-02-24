package ma.hmzelidrissi.backend.repositories;

import ma.hmzelidrissi.backend.domain.Ticket;
import ma.hmzelidrissi.backend.domain.Status;
import ma.hmzelidrissi.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByStatus(Status status);
    List<Ticket> findAllByCreatedBy(User user);
    List<Ticket> findAllByCreatedByAndStatus(User user, Status status);
    List<Ticket> findAllByTitleContainingIgnoreCase(String title);
    List<Ticket> findAllByTitleContainingIgnoreCaseAndCreatedBy(String title, User user);
    List<Ticket> findAllByIdAndCreatedBy(Long id, User user);
}