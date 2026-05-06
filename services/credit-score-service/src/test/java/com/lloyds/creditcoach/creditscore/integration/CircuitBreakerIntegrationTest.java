package com.lloyds.creditcoach.creditscore.integration;

import com.lloyds.creditcoach.creditscore.infrastructure.client.CraUnavailableException;
import com.lloyds.creditcoach.creditscore.infrastructure.client.ExperianCraClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CircuitBreakerIntegrationTest {

    @SpyBean
    private ExperianCraClient craClient;

    @Test
    void should_returnResponse_when_craAvailable() {
        UUID customerId = UUID.randomUUID();
        var response = craClient.retrieveScore(customerId);
        assertThat(response).isNotNull();
        assertThat(response.score()).isGreaterThan(0);
    }
}
