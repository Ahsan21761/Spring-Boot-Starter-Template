package com.example.taskmanager.task;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskmanager.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class TaskControllerIT extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void tasksRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    void fullCrudLifecycle() throws Exception {
        String token = registerAndGetToken("crud@example.com", "S3curePass!");

        // Create
        String createBody = """
                {"title":"First task","description":"do it","dueDate":"2026-09-01"}
                """;
        String createdJson = mockMvc.perform(post("/api/v1/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.status", is("TODO")))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(createdJson).get("id").asLong();

        // List
        mockMvc.perform(get("/api/v1/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("First task")));

        // Update
        String updateBody = """
                {"title":"First task","status":"DONE"}
                """;
        mockMvc.perform(put("/api/v1/tasks/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DONE")));

        // Delete
        mockMvc.perform(delete("/api/v1/tasks/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        // Gone
        mockMvc.perform(get("/api/v1/tasks/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void usersCannotAccessOthersTasks() throws Exception {
        String ownerToken = registerAndGetToken("owner@example.com", "S3curePass!");
        String otherToken = registerAndGetToken("intruder@example.com", "S3curePass!");

        String createdJson = mockMvc.perform(post("/api/v1/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"private\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(createdJson).get("id").asLong();

        // The intruder must not see the owner's task (ownership scoping => 404, not 403 leak).
        mockMvc.perform(get("/api/v1/tasks/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + otherToken))
                .andExpect(status().isNotFound());
    }

    private String registerAndGetToken(String email, String password) throws Exception {
        String body = """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);
        String json = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        return node.get("accessToken").asText();
    }
}
