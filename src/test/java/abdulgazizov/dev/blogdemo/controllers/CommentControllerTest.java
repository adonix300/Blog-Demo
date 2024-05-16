package abdulgazizov.dev.blogdemo.controllers;

import abdulgazizov.dev.blogdemo.TestContainerConfig;
import abdulgazizov.dev.blogdemo.models.dto.CommentDto;
import abdulgazizov.dev.blogdemo.models.entities.PostEntity;
import abdulgazizov.dev.blogdemo.models.entities.UserEntity;
import abdulgazizov.dev.blogdemo.models.user.CustomUserDetails;
import abdulgazizov.dev.blogdemo.models.user.Role;
import abdulgazizov.dev.blogdemo.repositories.PostRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestContainerConfig.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
@Transactional
class CommentControllerTest {
    private final static String AUTHORIZATION = "Authorization";
    private final static String BEARER = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtService jwtService;

    private String token;
    private Long postId;
    private CommentDto commentDto;

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
        String username = "test_user";
        token = signupUser(username, "password");

        PostEntity postEntity = PostEntity.builder()
                .title("Post Title")
                .content("Post Content")
                .user(userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found")))
                .build();

        postEntity = postRepository.saveAndFlush(postEntity);
        postId = postEntity.getId();

        commentDto = CommentDto.builder()
                .content("Comment Content")
                .build();
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
    public void getAllComments_testSuccess() throws Exception {
        mockMvc.perform(get("/posts/{postId}/comments", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void getAllComments_testForbidden() throws Exception {
        mockMvc.perform(get("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createComment_testSuccess() throws Exception {
        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createComment_testForbidden() throws Exception {
        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createComment_testValidationFailure() throws Exception {
        CommentDto emptyCommentDto = CommentDto.builder()
                .content("")
                .build();

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteComment_testSuccess() throws Exception {
        String commentResponse = mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long commentId = JsonPath.parse(commentResponse).read("$.id", Long.class);

        mockMvc.perform(delete("/posts/{postId}/comments/{id}", postId, commentId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment successfully deleted"));
    }

    @Test
    public void deleteComment_testForbidden() throws Exception {
        String commentResponse = mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long commentId = JsonPath.parse(commentResponse).read("$.id", Long.class);

        String token2 = signupUser("new_user", "password");

        mockMvc.perform(delete("/posts/{postId}/comments/{id}", postId, commentId)
                        .header(AUTHORIZATION, BEARER + token2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteComment_testBadRequest() throws Exception {
        String commentResponse = mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long commentId = JsonPath.parse(commentResponse).read("$.id", Long.class);

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 999L, commentId)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteComment_testNotFound() throws Exception {
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, 999L)
                        .header(AUTHORIZATION, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}