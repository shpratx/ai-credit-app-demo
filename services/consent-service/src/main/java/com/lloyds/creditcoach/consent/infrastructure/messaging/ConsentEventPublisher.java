package com.lloyds.creditcoach.consent.infrastructure.messaging;

import com.lloyds.creditcoach.consent.domain.model.Consent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ConsentEventPublisher.class);

    @Value("${app.pubsub.topic.consent-granted:credit-coach.consent.granted}")
    private String consentGrantedTopic;

    @Value("${app.pubsub.topic.consent-revoked:credit-coach.consent.revoked}")
    private String consentRevokedTopic;

    public void publishConsentGranted(Consent consent) {
        log.info("Publishing consent.granted event: consentId={}, customerId={}, provider={}",
                consent.getId(), consent.getCustomerId(), consent.getCraProvider());
        // In production: use PubSubTemplate.publish(consentGrantedTopic, payload)
    }

    public void publishConsentRevoked(Consent consent) {
        log.info("Publishing consent.revoked event: consentId={}, customerId={}, provider={}",
                consent.getId(), consent.getCustomerId(), consent.getCraProvider());
        // In production: use PubSubTemplate.publish(consentRevokedTopic, payload)
    }
}
