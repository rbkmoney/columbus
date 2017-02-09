package com.rbkmoney.columbus;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.ClusterWait;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.rbkmoney.columbus.check.LogInterceptor;
import com.rbkmoney.columbus.check.PostgresIsReadyCheck;
import org.joda.time.Duration;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = ColumbusApplication.class, initializers = AbstractIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {
    public  static LogInterceptor logInterceptor = new LogInterceptor();

    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose.yml")
            .logCollector(logInterceptor)
            .addClusterWait(new ClusterWait(new PostgresIsReadyCheck(logInterceptor.getIn()), Duration.standardMinutes(2)))
            .waitingForService("postgres", HealthChecks.toHaveAllPortsOpen())
            .build();

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            DockerPort postgres = docker.containers().container("postgres").port(5432);
            final String dbUrl = postgres.inFormat("jdbc:postgresql://$HOST:$EXTERNAL_PORT/postgres");

            EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
                    "spring.datasource.url=" + dbUrl
            );
        }
    }

    @Value("${local.server.port}")
    protected int port;
}
