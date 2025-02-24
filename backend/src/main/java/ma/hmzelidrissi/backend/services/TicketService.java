package ma.hmzelidrissi.backend.services;

import java.util.List;
import ma.hmzelidrissi.backend.domain.Status;
import ma.hmzelidrissi.backend.dtos.ticket.CommentDto;
import ma.hmzelidrissi.backend.dtos.ticket.CreateTicketRequestDto;
import ma.hmzelidrissi.backend.dtos.ticket.TicketDetailDto;
import ma.hmzelidrissi.backend.dtos.ticket.TicketSummaryDto;

public interface TicketService {
    TicketDetailDto createTicket(CreateTicketRequestDto request, String creatorEmail);

    List<TicketSummaryDto> getAllTickets(Status status, String userEmail);

    TicketDetailDto getTicketById(Long ticketId, String userEmail);

    TicketDetailDto updateTicketStatus(Long ticketId, Status newStatus, String userEmail);

    CommentDto addComment(Long ticketId, String content, String userEmail);

    List<TicketSummaryDto> searchTickets(String searchTerm, String userEmail);
}
