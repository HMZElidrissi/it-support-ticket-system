package ma.hmzelidrissi.backend.repositories;

import ma.hmzelidrissi.backend.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}