package com.icando;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ICanDoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ICanDoApplication.class, args);
    }

}
