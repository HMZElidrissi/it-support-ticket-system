package ma.hmzelidrissi.backend.dtos.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequestDto(
    @NotBlank(message = "Comment content is required")
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        String content) {}
