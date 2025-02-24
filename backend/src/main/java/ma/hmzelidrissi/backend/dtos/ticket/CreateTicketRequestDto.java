package ma.hmzelidrissi.backend.dtos.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.hmzelidrissi.backend.domain.Category;
import ma.hmzelidrissi.backend.domain.Priority;

public record CreateTicketRequestDto(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Priority is required")
        Priority priority,

        @NotNull(message = "Category is required")
        Category category
) {}
