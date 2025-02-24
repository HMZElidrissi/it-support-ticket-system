package ma.hmzelidrissi.backend.dtos.ticket;

import ma.hmzelidrissi.backend.domain.Category;
import ma.hmzelidrissi.backend.domain.Priority;
import ma.hmzelidrissi.backend.domain.Status;

import java.time.LocalDateTime;

public record TicketSummaryDto(
        Long id,
        String title,
        Priority priority,
        Category category,
        Status status,
        LocalDateTime creationDate,
        String createdByName
) {}