package com.lloyds.creditcoach.conversation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class CreditCoachConversationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditCoachConversationApplication.class, args);
    }
}
