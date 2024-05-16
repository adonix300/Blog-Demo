package abdulgazizov.dev.blogdemo;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
public class TestContainerConfig {

    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("blog")
                .withUsername("postgres")
                .withPassword("postgres");
        container.start();
        return container;
    }

    @Bean
    public DataSource dataSource(PostgreSQLContainer<?> postgresContainer) {
        return DataSourceBuilder.create()
                .url(postgresContainer.getJdbcUrl())
                .username(postgresContainer.getUsername())
                .password(postgresContainer.getPassword())
                .driverClassName(postgresContainer.getDriverClassName())
                .build();
    }
}
