package com.librarium;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.librarium.repository")
public class MainApplication {
    public static void main(String[] args) {
            SpringApplication.run(MainApplication.class, args);
    }
}
