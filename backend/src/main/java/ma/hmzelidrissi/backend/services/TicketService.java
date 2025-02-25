package ma.hmzelidrissi.backend.services;

import java.util.List;
import ma.hmzelidrissi.backend.domain.Status;
import ma.hmzelidrissi.backend.dtos.ticket.CommentDto;
import ma.hmzelidrissi.backend.dtos.ticket.CreateTicketRequestDto;
import ma.hmzelidrissi.backend.dtos.ticket.TicketDetailDto;
import ma.hmzelidrissi.backend.dtos.ticket.TicketSummaryDto;

public interface TicketService {
    TicketDetailDto createTicket(CreateTicketRequestDto request);

    List<TicketSummaryDto> getAllTickets(Status status);

    TicketDetailDto getTicketById(Long ticketId);

    TicketDetailDto updateTicketStatus(Long ticketId, Status newStatus);

    CommentDto addComment(Long ticketId, String content);

    List<TicketSummaryDto> searchTickets(String searchTerm);

    List<CommentDto> getTicketComments(Long ticketId);
}
