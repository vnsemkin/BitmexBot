package bitmexbot.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("test")
@Configuration
@ComponentScan(basePackages = {"bitmexbot"})
@PropertySource("classpath:application-test.properties")
public class TestConfiguration {

}
