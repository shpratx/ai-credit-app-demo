package com.lloyds.offerservice.unit;

import com.lloyds.offerservice.application.command.AcceptOfferCommand;
import com.lloyds.offerservice.application.command.AcceptOfferCommandHandler;
import com.lloyds.offerservice.application.dto.AcceptOfferResponse;
import com.lloyds.offerservice.application.dto.OfferResponse;
import com.lloyds.offerservice.application.dto.SecciDocument;
import com.lloyds.offerservice.application.query.*;
import com.lloyds.offerservice.domain.exception.BusinessRuleException;
import com.lloyds.offerservice.domain.model.*;
import com.lloyds.offerservice.domain.port.OfferAuditRepository;
import com.lloyds.offerservice.domain.port.OfferRepository;
import com.lloyds.offerservice.infrastructure.client.DistressIndicatorService;
import com.lloyds.offerservice.infrastructure.client.EligibilityMatchingClient;
import com.lloyds.offerservice.infrastructure.messaging.OfferEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Offer Service Handlers")
class OfferServiceHandlerTest {

    @Mock private OfferRepository offerRepository;
    @Mock private OfferAuditRepository auditRepository;
    @Mock private DistressIndicatorService distressService;
    @Mock private EligibilityMatchingClient eligibilityClient;
    @Mock private OfferEventPublisher eventPublisher;

    private GetOffersQueryHandler getOffersHandler;
    private AcceptOfferCommandHandler acceptHandler;
    private GetSecciQueryHandler secciHandler;

    @BeforeEach
    void setUp() {
        getOffersHandler = new GetOffersQueryHandler(distressService, eligibilityClient, offerRepository, auditRepository, eventPublisher);
        acceptHandler = new AcceptOfferCommandHandler(offerRepository, auditRepository, eventPublisher);
        secciHandler = new GetSecciQueryHandler(offerRepository);
    }

    @Test
    @DisplayName("should return offers with representative example when customer eligible")
    void should_returnOffers_when_customerEligible() {
        UUID customerId = UUID.randomUUID();
        var offer = buildOffer(customerId);

        when(distressService.isCustomerInDistress(customerId)).thenReturn(false);
        when(offerRepository.findAvailableByCustomerId(customerId)).thenReturn(List.of(offer));
        when(auditRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<OfferResponse> result = getOffersHandler.handle(new GetOffersQuery(customerId));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).representativeExample()).isNotNull();
        assertThat(result.get(0).representativeExample().representativeApr()).isEqualTo(new BigDecimal("6.90"));
        verify(eventPublisher).publishOfferPresented(offer.getId(), customerId);
    }

    @Test
    @DisplayName("should suppress offers when customer in financial distress (FR-37)")
    void should_suppressOffers_when_customerInDistress() {
        UUID customerId = UUID.randomUUID();
        when(distressService.isCustomerInDistress(customerId)).thenReturn(true);

        List<OfferResponse> result = getOffersHandler.handle(new GetOffersQuery(customerId));

        assertThat(result).isEmpty();
        verify(offerRepository, never()).findAvailableByCustomerId(any());
    }

    @Test
    @DisplayName("should accept offer with cooling-off info when offer valid")
    void should_acceptOffer_when_offerValid() {
        UUID offerId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        var offer = buildOffer(customerId);
        offer.setId(offerId);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AcceptOfferResponse result = acceptHandler.handle(new AcceptOfferCommand(offerId, customerId));

        assertThat(result.status()).isEqualTo("ACCEPTED");
        assertThat(result.coolingOffNotice()).contains("14 days");
        assertThat(result.coolingOffExpiry()).isAfter(Instant.now().plus(13, ChronoUnit.DAYS));
        assertThat(result.earlyRepaymentFeePercent()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.latePaymentFeeCap()).isEqualByComparingTo(new BigDecimal("12.00"));
        verify(eventPublisher).publishOfferAccepted(offerId, customerId);
    }

    @Test
    @DisplayName("should generate SECCI with all CCA s.55A prescribed fields")
    void should_generateSecci_when_offerExists() {
        UUID offerId = UUID.randomUUID();
        var offer = buildOffer(UUID.randomUUID());
        offer.setId(offerId);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        SecciDocument secci = secciHandler.handle(new GetSecciQuery(offerId));

        assertThat(secci.creditorName()).isEqualTo("Lloyds Bank plc");
        assertThat(secci.totalAmountOfCredit()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(secci.representativeApr()).isEqualByComparingTo(new BigDecimal("6.90"));
        assertThat(secci.coolingOffPeriodDays()).isEqualTo(14);
        assertThat(secci.earlyRepaymentFeePercent()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(secci.latePaymentFee()).isEqualByComparingTo(new BigDecimal("12.00"));
        assertThat(secci.rightOfWithdrawal()).contains("CCA s.66A");
        assertThat(secci.regulatoryAuthority()).contains("FCA");
    }

    private PreApprovedOffer buildOffer(UUID customerId) {
        var offer = new PreApprovedOffer();
        offer.setCustomerId(customerId);
        offer.setProductId(UUID.randomUUID());
        offer.setAmount(new BigDecimal("10000"));
        offer.setRate(new BigDecimal("0.0350"));
        offer.setApr(new BigDecimal("6.90"));
        offer.setTerm(36);
        offer.setMonthlyPayment(new BigDecimal("305.50"));
        offer.setTotalPayable(new BigDecimal("10998.00"));
        offer.setTotalChargeForCredit(new BigDecimal("998.00"));
        offer.setStatus(OfferStatus.AVAILABLE);
        offer.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));
        return offer;
    }
}
