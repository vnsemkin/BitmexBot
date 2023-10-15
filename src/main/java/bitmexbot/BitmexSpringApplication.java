package bitmexbot;

import bitmexbot.config.BotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BitmexSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotConfiguration.class, args);
    }
}
