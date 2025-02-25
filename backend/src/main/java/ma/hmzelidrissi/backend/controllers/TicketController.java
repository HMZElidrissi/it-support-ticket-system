package ma.hmzelidrissi.backend.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.hmzelidrissi.backend.domain.Status;
import ma.hmzelidrissi.backend.dtos.ticket.*;
import ma.hmzelidrissi.backend.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
  private final TicketService ticketService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("isAuthenticated()")
  public TicketDetailDto createTicket(@Valid @RequestBody CreateTicketRequestDto request) {
    return ticketService.createTicket(request);
  }

  @GetMapping
  public List<TicketSummaryDto> getAllTickets(@RequestParam(required = false) Status status) {
    return ticketService.getAllTickets(status);
  }

  @GetMapping("/{id}")
  public TicketDetailDto getTicketById(@PathVariable Long id) {
    return ticketService.getTicketById(id);
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('IT_SUPPORT')")
  public TicketDetailDto updateTicketStatus(@PathVariable Long id, @RequestParam Status status) {
    return ticketService.updateTicketStatus(id, status);
  }

  @PostMapping("/{id}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('IT_SUPPORT')")
  public CommentDto addComment(
      @PathVariable Long id, @RequestBody @Valid AddCommentRequestDto request) {
    return ticketService.addComment(id, request.content());
  }

  @GetMapping("/{id}/comments")
  public List<CommentDto> getTicketComments(@PathVariable Long id) {
      return ticketService.getTicketComments(id);
  }

  @GetMapping("/search")
  public List<TicketSummaryDto> searchTickets(@RequestParam String term) {
    return ticketService.searchTickets(term);
  }
}
