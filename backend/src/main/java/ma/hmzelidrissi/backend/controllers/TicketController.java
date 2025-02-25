package ma.hmzelidrissi.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.hmzelidrissi.backend.domain.Status;
import ma.hmzelidrissi.backend.dtos.ticket.*;
import ma.hmzelidrissi.backend.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
  private final TicketService ticketService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("isAuthenticated()")
  public TicketDetailDto createTicket(@Valid @RequestBody CreateTicketRequestDto request) {
    return ticketService.createTicket(request, getUserEmail());
  }

  @GetMapping
  public List<TicketSummaryDto> getAllTickets(@RequestParam(required = false) Status status) {
    return ticketService.getAllTickets(status, getUserEmail());
  }

  @GetMapping("/{id}")
  public TicketDetailDto getTicketById(@PathVariable Long id) {
    return ticketService.getTicketById(id, getUserEmail());
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('IT_SUPPORT')")
  public TicketDetailDto updateTicketStatus(@PathVariable Long id, @RequestParam Status status) {
    return ticketService.updateTicketStatus(id, status, getUserEmail());
  }

  @PostMapping("/{id}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('IT_SUPPORT')")
  public CommentDto addComment(
      @PathVariable Long id, @RequestBody @Valid AddCommentRequestDto request) {
    return ticketService.addComment(id, request.content(), getUserEmail());
  }

  @GetMapping("/search")
  public List<TicketSummaryDto> searchTickets(@RequestParam String term) {
    return ticketService.searchTickets(term, getUserEmail());
  }

  private String getUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }
}
