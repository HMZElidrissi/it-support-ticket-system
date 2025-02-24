package ma.hmzelidrissi.backend.dtos.ticket;

import ma.hmzelidrissi.backend.domain.Status;

import java.time.LocalDateTime;

public record AuditLogDto(
        Long id,
        String action,
        LocalDateTime timestamp,
        String performedBy,
        Status oldStatus,
        Status newStatus,
        Long commentId
) {}