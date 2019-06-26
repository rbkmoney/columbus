package com.rbkmoney.columbus;

import com.rbkmoney.columbus.check.LogInterceptor;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = ColumbusApplication.class, initializers = AbstractIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public abstract class AbstractIntegrationTest {
    public  static LogInterceptor logInterceptor = new LogInterceptor();

    @ClassRule
    public static DockerComposeContainer compose =
            new DockerComposeContainer(
                    new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("postgres", 5432);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String serviceHost = compose.getServiceHost("postgres", 5432);
            TestPropertyValues
                    .of("spring.datasource.url=" + "jdbc:postgresql://" + serviceHost + ":" + 5432 + "/postgres")
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Value("${local.server.port}")
    protected int port;
}
