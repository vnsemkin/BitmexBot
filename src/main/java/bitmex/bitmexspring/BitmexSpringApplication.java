package bitmex.bitmexspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BitmexSpringApplication {
    public static void main(String[] args) {SpringApplication.run(BitmexSpringApplication.class, args);
    }
}
