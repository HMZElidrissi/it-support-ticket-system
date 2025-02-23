package ma.hmzelidrissi.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.hmzelidrissi.backend.dtos.auth.AuthenticationResponseDto;
import ma.hmzelidrissi.backend.dtos.auth.SigninRequestDto;
import ma.hmzelidrissi.backend.dtos.auth.SignupRequestDto;
import ma.hmzelidrissi.backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/signup")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthenticationResponseDto signup(@Valid @RequestBody SignupRequestDto request) {
    return authService.signup(request);
  }

  @PostMapping("/signin")
  public AuthenticationResponseDto signin(@Valid @RequestBody SigninRequestDto request) {
    return authService.signin(request);
  }
}
