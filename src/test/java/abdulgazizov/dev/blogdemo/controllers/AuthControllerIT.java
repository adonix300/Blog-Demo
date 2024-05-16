package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.TestContainerConfig;
import abdulgazizov.dev.blogdemo.models.dto.AuthRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestContainerConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("password");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE comments CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE posts CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    @Test
    public void signUp_testSuccess() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void signIn_testSuccess() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void signUp_testWithExistingUsername() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signIn_testWithWrongPassword() throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated());

        AuthRequest wrongPasswordRequest = new AuthRequest();
        wrongPasswordRequest.setUsername(authRequest.getUsername());
        wrongPasswordRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signIn_testWithNonExistentUser() throws Exception {
        AuthRequest nonExistentUserRequest = new AuthRequest();
        nonExistentUserRequest.setUsername("nonexistentuser");
        nonExistentUserRequest.setPassword("password");

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signUp_testWithEmptyUsername() throws Exception {
        AuthRequest emptyUsernameRequest = new AuthRequest();
        emptyUsernameRequest.setUsername("");
        emptyUsernameRequest.setPassword("password");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyUsernameRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signUp_testWithEmptyPassword() throws Exception {
        AuthRequest emptyPasswordRequest = new AuthRequest();
        emptyPasswordRequest.setUsername("user_" + UUID.randomUUID());
        emptyPasswordRequest.setPassword("");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPasswordRequest)))
                .andExpect(status().isBadRequest());
    }
}