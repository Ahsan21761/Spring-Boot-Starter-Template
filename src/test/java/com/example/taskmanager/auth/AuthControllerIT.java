package com.example.taskmanager.auth;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskmanager.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void registerReturns201WithToken() throws Exception {
        String body = """
                {"email":"newuser@example.com","password":"S3curePass!","fullName":"New User"}
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.tokenType", is("Bearer")));
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        String body = """
                {"email":"dupe@example.com","password":"S3curePass!"}
                """;
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    void invalidPayloadReturns400WithFieldErrors() throws Exception {
        String body = """
                {"email":"not-an-email","password":"short"}
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", notNullValue()));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        String register = """
                {"email":"loginuser@example.com","password":"CorrectPass1!"}
                """;
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated());

        String wrong = """
                {"email":"loginuser@example.com","password":"WrongPass1!"}
                """;
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(wrong))
                .andExpect(status().isUnauthorized());
    }
}
