package ma.hmzelidrissi.backend.services;

import ma.hmzelidrissi.backend.dtos.auth.AuthenticationResponseDto;
import ma.hmzelidrissi.backend.dtos.auth.SigninRequestDto;
import ma.hmzelidrissi.backend.dtos.auth.SignupRequestDto;

public interface AuthService {

    AuthenticationResponseDto signup(SignupRequestDto request);

    AuthenticationResponseDto signin(SigninRequestDto request);
}
