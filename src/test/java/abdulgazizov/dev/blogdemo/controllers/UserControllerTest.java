package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.TestContainerConfig;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.models.user.CustomUserDetails;
import abdulgazizov.dev.blogdemo.models.user.Role;
import abdulgazizov.dev.blogdemo.repositories.UserRepository;
import abdulgazizov.dev.blogdemo.secutiry.JwtService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestContainerConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserControllerTest {
    private final static String AUTHORIZATION = "Authorization";
    private final static String BEARER = "Bearer ";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private MockMvc mockMvc;

    private String token;

    private String signupUser(String username, String password) {
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(password)
                .role(Role.ROLE_USER)
                .build();

        userEntity = userRepository.saveAndFlush(userEntity);

        UserDetails userDetails = new CustomUserDetails(userEntity);

        return jwtService.generateToken(userDetails);
    }

    @BeforeEach
    void setUp() {
        token = signupUser("username", "password");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void profile_testSuccess() throws Exception {
        mockMvc.perform(get("/user/profile")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void profile_testForbidden() throws Exception {
        mockMvc.perform(get("/user/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void profileById_testSuccess() throws Exception {
        mockMvc.perform(get("/user/profile/{id}", 1L)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"));

        signupUser("username2", "password2");

        mockMvc.perform(get("/user/profile/{id}", 2L)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username2"));
    }

    @Test
    public void profileById_testFailNotFound() throws Exception {
        mockMvc.perform(get("/user/profile/{id}", 999L)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void profileById_testForbidden() throws Exception {
        mockMvc.perform(get("/user/profile/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}