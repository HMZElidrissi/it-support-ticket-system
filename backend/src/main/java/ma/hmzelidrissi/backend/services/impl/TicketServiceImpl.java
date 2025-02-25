package ma.hmzelidrissi.backend.services.impl;

import lombok.RequiredArgsConstructor;
import ma.hmzelidrissi.backend.exceptions.ResourceNotFoundException;
import ma.hmzelidrissi.backend.domain.*;
import ma.hmzelidrissi.backend.dtos.ticket.*;
import ma.hmzelidrissi.backend.mappers.TicketMapper;
import ma.hmzelidrissi.backend.repositories.CommentRepository;
import ma.hmzelidrissi.backend.repositories.TicketRepository;
import ma.hmzelidrissi.backend.repositories.UserRepository;
import ma.hmzelidrissi.backend.repositories.AuditLogRepository;
import ma.hmzelidrissi.backend.services.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    @Override
    public TicketDetailDto createTicket(CreateTicketRequestDto request) {
        User creator = getCurrentUserByEmail();

        Ticket ticket = Ticket.builder()
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .category(request.category())
                .status(Status.NEW)
                .createdBy(creator)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        AuditLog auditLog = AuditLog.builder()
                .ticket(savedTicket)
                .action("Ticket created")
                .performedBy(creator)
                .newStatus(Status.NEW)
                .build();

        auditLogRepository.save(auditLog);

        return TicketMapper.mapToTicketDetailDto(savedTicket);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TicketSummaryDto> getAllTickets(Status status) {
        User user = getCurrentUserByEmail();
        List<Ticket> tickets;

        // If IT_SUPPORT, can see all tickets, otherwise only own tickets
        if (user.getRole() == Role.IT_SUPPORT) {
            if (status != null) {
                tickets = ticketRepository.findAllByStatus(status);
            } else {
                tickets = ticketRepository.findAll();
            }
        } else {
            if (status != null) {
                tickets = ticketRepository.findAllByCreatedByAndStatus(user, status);
            } else {
                tickets = ticketRepository.findAllByCreatedBy(user);
            }
        }

        return tickets.stream()
                .map(TicketMapper::mapToTicketSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public TicketDetailDto getTicketById(Long ticketId) {
        User user = getCurrentUserByEmail();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        // Check if user has access to the ticket (IT_SUPPORT can access all tickets)
        if (user.getRole() != Role.IT_SUPPORT && !ticket.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have access to this ticket");
        }

        return TicketMapper.mapToTicketDetailDto(ticket);
    }

    @Transactional
    @Override
    public TicketDetailDto updateTicketStatus(Long ticketId, Status newStatus) {
        User user = getCurrentUserByEmail();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        Status oldStatus = ticket.getStatus();

        if (oldStatus == newStatus) {
            return TicketMapper.mapToTicketDetailDto(ticket);
        }

        ticket.setStatus(newStatus);

        AuditLog auditLog = AuditLog.builder()
                .ticket(ticket)
                .action("Status changed from " + oldStatus + " to " + newStatus)
                .performedBy(user)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .build();

        auditLogRepository.save(auditLog);
        ticketRepository.save(ticket);

        return TicketMapper.mapToTicketDetailDto(ticket);
    }

    @Transactional
    @Override
    public CommentDto addComment(Long ticketId, String content) {
        User user = getCurrentUserByEmail();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        Comment comment = Comment.builder()
                .content(content)
                .ticket(ticket)
                .createdBy(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        AuditLog auditLog = AuditLog.builder()
                .ticket(ticket)
                .action("Comment added")
                .performedBy(user)
                .comment(savedComment)
                .build();

        auditLogRepository.save(auditLog);

        return TicketMapper.mapToCommentDto(savedComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TicketSummaryDto> searchTickets(String searchTerm) {
        User user = getCurrentUserByEmail();
        List<Ticket> tickets;

        Long ticketId = null;
        try {
            ticketId = Long.parseLong(searchTerm);
        } catch (NumberFormatException ignored) {
            // Not a number, will search by title => ignore
        }

        if (user.getRole() == Role.IT_SUPPORT) {
            if (ticketId != null) {
                tickets = ticketRepository.findAllById(List.of(ticketId));
            } else {
                tickets = ticketRepository.findAllByTitleContainingIgnoreCase(searchTerm);
            }
        } else {
            if (ticketId != null) {
                tickets = ticketRepository.findAllByIdAndCreatedBy(ticketId, user);
            } else {
                tickets = ticketRepository.findAllByTitleContainingIgnoreCaseAndCreatedBy(searchTerm, user);
            }
        }

        return tickets.stream()
                .map(TicketMapper::mapToTicketSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getTicketComments(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        return ticket.getComments().stream()
                .map(TicketMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    private User getCurrentUserByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}