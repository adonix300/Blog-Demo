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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestContainerConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@Transactional
class UserControllerTest {
    private final static String AUTHORIZATION = "Authorization";
    private final static String BEARER = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        clearDataBase();
        SecurityContextHolder.clearContext();
    }

    void clearDataBase() {
        jdbcTemplate.execute("TRUNCATE TABLE comments CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE posts CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
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
        UserEntity userEntity = UserEntity.builder()
                .username("NEW_USERNAME")
                .password("PASSWORD")
                .role(Role.ROLE_USER)
                .build();

        userEntity = userRepository.saveAndFlush(userEntity);

        UserDetails userDetails = new CustomUserDetails(userEntity);

        String token = jwtService.generateToken(userDetails);

        mockMvc.perform(get("/user/profile/{id}", userEntity.getId())
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        UserEntity userEntity2 = UserEntity.builder()
                .username("NEW_USERNAME_2")
                .password("PASSWORD")
                .role(Role.ROLE_USER)
                .build();

        userEntity2 = userRepository.saveAndFlush(userEntity);

        mockMvc.perform(get("/user/profile/{id}", userEntity2.getId())
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

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