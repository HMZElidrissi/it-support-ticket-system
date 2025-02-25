package ma.hmzelidrissi.frontend.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ma.hmzelidrissi.frontend.dto.AddCommentRequest;
import ma.hmzelidrissi.frontend.dto.CreateTicketRequest;
import ma.hmzelidrissi.frontend.dto.UpdateStatusRequest;
import ma.hmzelidrissi.frontend.model.*;
import ma.hmzelidrissi.frontend.utils.ApiClient;

import java.net.http.HttpResponse;
import java.util.List;

public class TicketApi {
    private final String BASE_URL = "/api/v1/tickets";
    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;

    public TicketApi() {
        this.apiClient = new ApiClient();

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Ticket> getAllTickets() throws Exception {
        HttpResponse<String> response = apiClient.get(BASE_URL);

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            throw new Exception("Failed to get tickets: " + response.body());
        }
    }

    public List<Ticket> getTicketsByStatus(Status status) throws Exception {
        HttpResponse<String> response = apiClient.get(BASE_URL + "?status=" + status);

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            throw new Exception("Failed to get tickets: " + response.body());
        }
    }

    public Ticket getTicketById(Long id) throws Exception {
        HttpResponse<String> response = apiClient.get(BASE_URL + "/" + id);

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Ticket.class);
        } else if (response.statusCode() == 404) {
            return null;
        } else {
            throw new Exception("Failed to get ticket: " + response.body());
        }
    }

    public boolean createTicket(CreateTicketRequest request) throws Exception {
        String json = objectMapper.writeValueAsString(request);

        HttpResponse<String> response = apiClient.post(BASE_URL, json);

        return response.statusCode() == 201;
    }

    public boolean updateTicketStatus(Long id, UpdateStatusRequest request) throws Exception {
        String json = objectMapper.writeValueAsString(request);

        HttpResponse<String> response = apiClient.put(BASE_URL + "/" + id + "/status", json);

        return response.statusCode() == 200;
    }

    public List<Comment> getTicketComments(Long ticketId) throws Exception {
        HttpResponse<String> response = apiClient.get(BASE_URL + "/" + ticketId + "/comments");

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            throw new Exception("Failed to get comments: " + response.body());
        }
    }

    public Comment addComment(Long ticketId, AddCommentRequest request) throws Exception {
        String json = objectMapper.writeValueAsString(request);

        HttpResponse<String> response = apiClient.post(BASE_URL + "/" + ticketId + "/comments", json);

        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), Comment.class);
        } else {
            throw new Exception("Failed to add comment: " + response.body());
        }
    }
}