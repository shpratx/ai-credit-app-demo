package com.lloyds.creditcoach.consent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CreditCoachConsentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCoachConsentApplication.class, args);
    }
}
