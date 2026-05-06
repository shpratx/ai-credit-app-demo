package com.lloyds.creditcoach.creditscore.infrastructure.messaging;

import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScoreEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ScoreEventPublisher.class);

    public void publishScoreRetrieved(CreditScore score) {
        log.info("Publishing score.retrieved event: scoreId={}, customerId={}", score.getId(), score.getCustomerId());
    }

    public void publishScoreChanged(CreditScore score) {
        log.info("Publishing score.changed event: scoreId={}, customerId={}, change={}",
                score.getId(), score.getCustomerId(), score.getChange());
    }
}
