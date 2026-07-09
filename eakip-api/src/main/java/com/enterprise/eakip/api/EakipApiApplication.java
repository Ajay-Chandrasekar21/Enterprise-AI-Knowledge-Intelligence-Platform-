package com.enterprise.eakip.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = "com.enterprise.eakip")
@EntityScan(basePackages = "com.enterprise.eakip.core.domain.model")
@EnableJpaRepositories(basePackages = "com.enterprise.eakip.core.domain.repository")
public class EakipApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EakipApiApplication.class, args);
    }
}
