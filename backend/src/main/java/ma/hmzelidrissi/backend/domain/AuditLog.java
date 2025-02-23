package ma.hmzelidrissi.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ticket_id", nullable = false)
  private Ticket ticket;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false)
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performed_by", nullable = false)
  private User performedBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "old_status")
  private Status oldStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "new_status")
  private Status newStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;
}