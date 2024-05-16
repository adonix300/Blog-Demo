package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.TestContainerConfig;
import abdulgazizov.dev.blogdemo.models.dto.PostDto;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.models.user.CustomUserDetails;
import abdulgazizov.dev.blogdemo.models.user.Role;
import abdulgazizov.dev.blogdemo.repositories.UserRepository;
import abdulgazizov.dev.blogdemo.secutiry.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestContainerConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class PostControllerIT {
    private final static String AUTHORIZATION = "Authorization";
    private final static String BEARER = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;


    private PostDto postDto;

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
    public void setUp() {
        postDto = PostDto.builder()
                .title("Test Title")
                .content("Test Content")
                .build();

        token = signupUser("test_user", "test_password");
    }

    @AfterEach
    void tearDown() {
        clearDataBase();
        SecurityContextHolder.clearContext();
    }

    private void clearDataBase() {
        jdbcTemplate.execute("TRUNCATE TABLE comments CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE posts CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    @Test
    public void createPost_testSuccess() throws Exception {
        mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", containsString("Test Title")))
                .andExpect(jsonPath("$.content", containsString("Test Content")));
    }

    @Test
    public void createPost_testForbidden() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createPost_testValidationFailure() throws Exception {
        PostDto postDto = PostDto.builder()
                .title("")
                .content("Test Content")
                .build();

        mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllPosts_testSuccess() throws Exception {
        mockMvc.perform(get("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void getAllPosts_testForbidden() throws Exception {
        mockMvc.perform(get("/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getPostById_testSuccess() throws Exception {
        String postResponse = mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long postId = JsonPath.parse(postResponse).read("$.id", Long.class);

        mockMvc.perform(get("/posts/{id}", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", containsString("Test Title")))
                .andExpect(jsonPath("$.content", containsString("Test Content")));
    }

    @Test
    public void getPostById_testNotFound() throws Exception {
        mockMvc.perform(get("/posts/{id}", 999L)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePost_testForbidden() throws Exception {
        String postResponse = mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long postId = JsonPath.parse(postResponse).read("$.id", Long.class);

        String token2 = signupUser("new_user", "new_password");

        PostDto updatedPostDto = PostDto.builder()
                .title("Updated Test Title")
                .content("Updated Test Content")
                .build();

        mockMvc.perform(put("/posts/{id}", postId)
                        .header(AUTHORIZATION, BEARER + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPostDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updatePost_testSuccess() throws Exception {
        String postResponse = mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long postId = JsonPath.parse(postResponse).read("$.id", Long.class);

        PostDto updatedPostDto = PostDto.builder()
                .title("Updated Test Title")
                .content("Updated Test Content")
                .build();

        mockMvc.perform(put("/posts/{id}", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", containsString("Updated Test Title")))
                .andExpect(jsonPath("$.content", containsString("Updated Test Content")));
    }

    @Test
    public void updatePost_testNotFound() throws Exception {
        PostDto updatedPostDto = PostDto.builder()
                .title("Updated Test Title")
                .content("Updated Test Content")
                .build();

        mockMvc.perform(put("/posts/{id}", 999L)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPostDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePost_testSuccess() throws Exception {
        String postResponse = mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long postId = JsonPath.parse(postResponse).read("$.id", Long.class);

        mockMvc.perform(delete("/posts/{id}", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted post"));

        mockMvc.perform(get("/posts/{id}", postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePost_testForbidden() throws Exception {
        PostDto postDto = PostDto.builder()
                .title("Test Title")
                .content("Test Content")
                .build();

        String postResponse = mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long postId = JsonPath.parse(postResponse).read("$.id", Long.class);

        SecurityContextHolder.clearContext();
        String token2 = signupUser("new_user", "new_password");
        mockMvc.perform(delete("/posts/{id}", postId)
                        .header(AUTHORIZATION, BEARER + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deletePost_testNotFound() throws Exception {
        mockMvc.perform(delete("/posts/{id}", 999L)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}