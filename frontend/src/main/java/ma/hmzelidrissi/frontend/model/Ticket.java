package ma.hmzelidrissi.frontend.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Ticket {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;
    private LocalDateTime creationDate;
    private String createdByName;
    private List<Comment> comments = new ArrayList<>();
}