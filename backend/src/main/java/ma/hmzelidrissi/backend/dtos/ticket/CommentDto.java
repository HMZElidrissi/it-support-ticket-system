package ma.hmzelidrissi.backend.dtos.ticket;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        String createdBy
) {}