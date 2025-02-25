package ma.hmzelidrissi.frontend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.hmzelidrissi.frontend.dto.AuthResponse;
import ma.hmzelidrissi.frontend.dto.SigninRequest;
import ma.hmzelidrissi.frontend.dto.SignupRequest;
import ma.hmzelidrissi.frontend.utils.ApiClient;

import java.net.http.HttpResponse;

public class AuthApi {
  private final String BASE_URL = "/api/v1/auth";
  private final ApiClient apiClient;
  private final ObjectMapper objectMapper;

  public AuthApi() {
    this.apiClient = new ApiClient();
    this.objectMapper = new ObjectMapper();
  }

  public AuthResponse signin(SigninRequest request) throws Exception {
    String json = objectMapper.writeValueAsString(request);

    HttpResponse<String> response = apiClient.post(BASE_URL + "/signin", json);

    if (response.statusCode() == 200) {
      return objectMapper.readValue(response.body(), AuthResponse.class);
    } else {
      throw new Exception("Authentication failed: " + response.body());
    }
  }

  public AuthResponse signup(SignupRequest request) throws Exception {
    String json = objectMapper.writeValueAsString(request);

    HttpResponse<String> response = apiClient.post(BASE_URL + "/signup", json);

    if (response.statusCode() == 201) {
      return objectMapper.readValue(response.body(), AuthResponse.class);
    } else {
      throw new Exception("Registration failed: " + response.body());
    }
  }
}
