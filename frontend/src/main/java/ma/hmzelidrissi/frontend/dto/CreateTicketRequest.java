package ma.hmzelidrissi.frontend.dto;

import ma.hmzelidrissi.frontend.model.Category;
import ma.hmzelidrissi.frontend.model.Priority;

public record CreateTicketRequest(
        String title,
        String description,
        Priority priority,
        Category category
) {}