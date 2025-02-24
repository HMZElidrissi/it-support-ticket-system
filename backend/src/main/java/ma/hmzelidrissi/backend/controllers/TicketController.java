package ma.hmzelidrissi.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.hmzelidrissi.backend.domain.Status;
import ma.hmzelidrissi.backend.dtos.ticket.*;
import ma.hmzelidrissi.backend.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public TicketDetailDto createTicket(
            @Valid @RequestBody CreateTicketRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ticketService.createTicket(request, userDetails.getUsername());
    }

    @GetMapping
    public List<TicketSummaryDto> getAllTickets(
            @RequestParam(required = false) Status status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ticketService.getAllTickets(status, userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public TicketDetailDto getTicketById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ticketService.getTicketById(id, userDetails.getUsername());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public TicketDetailDto updateTicketStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ticketService.updateTicketStatus(id, status, userDetails.getUsername());
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public CommentDto addComment(
            @PathVariable Long id,
            @RequestBody @Valid AddCommentRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ticketService.addComment(id, request.content(), userDetails.getUsername());
    }

    @GetMapping("/search")
    public List<TicketSummaryDto> searchTickets(
            @RequestParam String term,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ticketService.searchTickets(term, userDetails.getUsername());
    }
}