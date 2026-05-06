package com.lloyds.creditcoach.creditscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CreditCoachScoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCoachScoreApplication.class, args);
    }
}
