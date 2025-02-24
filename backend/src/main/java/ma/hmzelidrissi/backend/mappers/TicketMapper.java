package ma.hmzelidrissi.backend.mappers;

import ma.hmzelidrissi.backend.domain.AuditLog;
import ma.hmzelidrissi.backend.domain.Comment;
import ma.hmzelidrissi.backend.domain.Ticket;
import ma.hmzelidrissi.backend.dtos.ticket.AuditLogDto;
import ma.hmzelidrissi.backend.dtos.ticket.CommentDto;
import ma.hmzelidrissi.backend.dtos.ticket.TicketDetailDto;
import ma.hmzelidrissi.backend.dtos.ticket.TicketSummaryDto;

import java.util.List;
import java.util.stream.Collectors;

public class TicketMapper {

    public static TicketSummaryDto mapToTicketSummaryDto(Ticket ticket) {
        return new TicketSummaryDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getStatus(),
                ticket.getCreationDate(),
                ticket.getCreatedBy().getName()
        );
    }

    public static TicketDetailDto mapToTicketDetailDto(Ticket ticket) {
        List<CommentDto> comments = ticket.getComments().stream()
                .map(TicketMapper::mapToCommentDto)
                .collect(Collectors.toList());

        List<AuditLogDto> auditLogs = ticket.getAuditLogs().stream()
                .map(TicketMapper::mapToAuditLogDto)
                .collect(Collectors.toList());

        return new TicketDetailDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getStatus(),
                ticket.getCreationDate(),
                ticket.getCreatedBy().getName(),
                comments,
                auditLogs
        );
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getCreatedBy().getName()
        );
    }

    public static AuditLogDto mapToAuditLogDto(AuditLog auditLog) {
        return new AuditLogDto(
                auditLog.getId(),
                auditLog.getAction(),
                auditLog.getTimestamp(),
                auditLog.getPerformedBy().getName(),
                auditLog.getOldStatus(),
                auditLog.getNewStatus(),
                auditLog.getComment() != null ? auditLog.getComment().getId() : null
        );
    }
}