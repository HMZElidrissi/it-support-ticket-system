package ma.hmzelidrissi.backend.dtos.auth;

import lombok.*;

@Builder
public record AuthenticationResponseDto(
    String token, String name, String email, String role) {}
