package by.cleverdev_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CleverdevTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleverdevTaskApplication.class, args);
    }
}
