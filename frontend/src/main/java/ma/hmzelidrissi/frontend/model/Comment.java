package ma.hmzelidrissi.frontend.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Comment {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String createdBy;
}