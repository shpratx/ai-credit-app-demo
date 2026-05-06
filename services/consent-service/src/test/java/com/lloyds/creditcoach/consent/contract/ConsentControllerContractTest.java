package com.lloyds.creditcoach.consent.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lloyds.creditcoach.consent.application.command.GrantConsentCommandHandler;
import com.lloyds.creditcoach.consent.application.command.WithdrawConsentCommandHandler;
import com.lloyds.creditcoach.consent.application.dto.ConsentResponse;
import com.lloyds.creditcoach.consent.application.dto.ConsentsListResponse;
import com.lloyds.creditcoach.consent.application.query.GetConsentsQueryHandler;
import com.lloyds.creditcoach.consent.api.controller.ConsentController;
import com.lloyds.creditcoach.consent.api.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ConsentControllerContractTest {

    private MockMvc mockMvc;

    @Mock
    private GrantConsentCommandHandler grantHandler;
    @Mock
    private WithdrawConsentCommandHandler withdrawHandler;
    @Mock
    private GetConsentsQueryHandler getConsentsHandler;
    @InjectMocks
    private ConsentController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void should_returnCreated_when_grantingConsent() throws Exception {
        UUID customerId = UUID.randomUUID();
        var response = new ConsentResponse(UUID.randomUUID(), customerId, "EXPERIAN", "GRANTED", Instant.now(), null);

        when(grantHandler.handle(any(), eq(customerId), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/credit-coach/consents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Customer-Id", customerId)
                        .header("X-Correlation-Id", UUID.randomUUID())
                        .content("""
                                {
                                    "craProvider": "EXPERIAN",
                                    "consentTextVersion": "1.0",
                                    "consentTextHash": "%s",
                                    "channel": "WEB",
                                    "privacyNoticeAccepted": true
                                }
                                """.formatted("a".repeat(64))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.consentId").exists())
                .andExpect(jsonPath("$.status").value("GRANTED"))
                .andExpect(jsonPath("$.craProvider").value("EXPERIAN"));
    }

    @Test
    void should_returnOk_when_gettingConsents() throws Exception {
        UUID customerId = UUID.randomUUID();
        var response = new ConsentsListResponse(List.of(
                new ConsentResponse(UUID.randomUUID(), customerId, "EXPERIAN", "GRANTED", Instant.now(), null)
        ));

        when(getConsentsHandler.handle(customerId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/credit-coach/consents/{customerId}", customerId)
                        .header("X-Correlation-Id", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("GRANTED"));
    }

    @Test
    void should_returnBadRequest_when_invalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/credit-coach/consents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Customer-Id", UUID.randomUUID())
                        .header("X-Correlation-Id", UUID.randomUUID())
                        .content("""
                                {
                                    "craProvider": "",
                                    "consentTextVersion": "",
                                    "consentTextHash": "",
                                    "channel": "",
                                    "privacyNoticeAccepted": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
