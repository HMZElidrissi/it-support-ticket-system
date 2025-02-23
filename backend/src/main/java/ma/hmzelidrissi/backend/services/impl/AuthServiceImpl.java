package ma.hmzelidrissi.backend.services.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import ma.hmzelidrissi.backend.domain.Role;
import ma.hmzelidrissi.backend.domain.User;
import ma.hmzelidrissi.backend.dtos.auth.AuthenticationResponseDto;
import ma.hmzelidrissi.backend.dtos.auth.SigninRequestDto;
import ma.hmzelidrissi.backend.dtos.auth.SignupRequestDto;
import ma.hmzelidrissi.backend.repositories.UserRepository;
import ma.hmzelidrissi.backend.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtEncoder jwtEncoder;

  private String generateToken(User user) {
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("ticket-system")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(15, ChronoUnit.DAYS))
            .subject(user.getEmail())
            .claim("role", user.getRole().name())
            .build();

    JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  @Override
  public AuthenticationResponseDto signup(SignupRequestDto request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new RuntimeException("Email already exists");
    }

    var user = User.builder()
            .name(request.name())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.EMPLOYEE)
            .build();

    userRepository.save(user);
    var jwt = generateToken(user);

    return AuthenticationResponseDto.builder()
            .token(jwt)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
  }

  @Override
  public AuthenticationResponseDto signin(SigninRequestDto request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

    var jwt = generateToken(user);

    return AuthenticationResponseDto.builder()
            .token(jwt)
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
  }
}
