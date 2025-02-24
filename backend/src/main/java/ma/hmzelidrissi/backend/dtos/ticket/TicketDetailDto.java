package ma.hmzelidrissi.backend.dtos.ticket;

import ma.hmzelidrissi.backend.domain.Category;
import ma.hmzelidrissi.backend.domain.Priority;
import ma.hmzelidrissi.backend.domain.Status;

import java.time.LocalDateTime;
import java.util.List;

public record TicketDetailDto(
        Long id,
        String title,
        String description,
        Priority priority,
        Category category,
        Status status,
        LocalDateTime creationDate,
        String createdBy,
        List<CommentDto> comments,
        List<AuditLogDto> auditLogs
) {}