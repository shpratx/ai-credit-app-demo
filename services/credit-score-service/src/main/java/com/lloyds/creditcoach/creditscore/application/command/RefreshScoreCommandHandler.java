package com.lloyds.creditcoach.creditscore.application.command;

import com.lloyds.creditcoach.creditscore.application.dto.ScoreResponse;
import com.lloyds.creditcoach.creditscore.application.dto.ScoreRetrievingResponse;
import com.lloyds.creditcoach.creditscore.domain.model.CraApiAuditLog;
import com.lloyds.creditcoach.creditscore.domain.model.CreditScore;
import com.lloyds.creditcoach.creditscore.domain.model.ScoreFactor;
import com.lloyds.creditcoach.creditscore.domain.port.AuditLogRepository;
import com.lloyds.creditcoach.creditscore.domain.port.CreditScoreRepository;
import com.lloyds.creditcoach.creditscore.domain.port.ScoreFactorRepository;
import com.lloyds.creditcoach.creditscore.infrastructure.client.CraResponse;
import com.lloyds.creditcoach.creditscore.infrastructure.client.ExperianCraClient;
import com.lloyds.creditcoach.creditscore.infrastructure.cache.RedisCacheService;
import com.lloyds.creditcoach.creditscore.infrastructure.encryption.EncryptionService;
import com.lloyds.creditcoach.creditscore.infrastructure.messaging.ScoreEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshScoreCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(RefreshScoreCommandHandler.class);
    private static final int DATA_QUALITY_THRESHOLD = 95;

    private final CreditScoreRepository scoreRepository;
    private final ScoreFactorRepository factorRepository;
    private final AuditLogRepository auditLogRepository;
    private final ExperianCraClient craClient;
    private final RedisCacheService cacheService;
    private final EncryptionService encryptionService;
    private final ScoreEventPublisher eventPublisher;

    public RefreshScoreCommandHandler(CreditScoreRepository scoreRepository,
                                      ScoreFactorRepository factorRepository,
                                      AuditLogRepository auditLogRepository,
                                      ExperianCraClient craClient,
                                      RedisCacheService cacheService,
                                      EncryptionService encryptionService,
                                      ScoreEventPublisher eventPublisher) {
        this.scoreRepository = scoreRepository;
        this.factorRepository = factorRepository;
        this.auditLogRepository = auditLogRepository;
        this.craClient = craClient;
        this.cacheService = cacheService;
        this.encryptionService = encryptionService;
        this.eventPublisher = eventPublisher;
    }

    public ScoreRetrievingResponse handleRefresh(UUID customerId, UUID correlationId) {
        // Rate limit: max 1 refresh per 24hr
        scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN")
                .ifPresent(latest -> {
                    if (latest.getRetrievedAt().isAfter(Instant.now().minus(Duration.ofHours(24)))) {
                        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Max 1 refresh per 24 hours");
                    }
                });

        // Trigger async retrieval
        log.info("Triggering score refresh for customerId={}", customerId);
        return new ScoreRetrievingResponse("retrieving", 3);
    }

    @Transactional
    public ScoreResponse handleRetrieval(UUID customerId, UUID correlationId) {
        long startTime = System.currentTimeMillis();

        // Check cache first
        ScoreResponse cached = cacheService.getScore(customerId);
        if (cached != null) {
            return cached;
        }

        // Call CRA
        CraResponse craResponse;
        try {
            craResponse = craClient.retrieveScore(customerId);
        } catch (Exception ex) {
            log.warn("CRA call failed, attempting stale cache fallback", ex);
            ScoreResponse stale = cacheService.getStaleScore(customerId);
            if (stale != null) {
                return new ScoreResponse(stale.customerId(), stale.provider(), stale.score(),
                        stale.maxScore(), stale.band(), stale.previousScore(), stale.change(),
                        stale.changeDirection(), stale.retrievedAt(), true, "cache_stale");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "CRA unavailable");
        }

        // Validate data quality
        if (craResponse.dataQualityScore() < DATA_QUALITY_THRESHOLD) {
            logAudit(customerId, "ERROR", correlationId, System.currentTimeMillis() - startTime);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Data quality below threshold");
        }

        // Encrypt score
        byte[] encryptedScore = encryptionService.encrypt(String.valueOf(craResponse.score()));

        // Compute change
        var previousOpt = scoreRepository.findLatestByCustomerAndProvider(customerId, "EXPERIAN");
        Integer previousValue = previousOpt.map(p -> Integer.parseInt(encryptionService.decrypt(p.getScoreValue()))).orElse(null);
        Integer change = previousValue != null ? craResponse.score() - previousValue : null;
        String changeDirection = change == null ? null : change > 0 ? "up" : change < 0 ? "down" : "unchanged";

        // Store score
        var score = new CreditScore();
        score.setCustomerId(customerId);
        score.setProvider("EXPERIAN");
        score.setScoreValue(encryptedScore);
        score.setMaxScore(999);
        score.setBand(CreditScore.classifyBand(craResponse.score()));
        score.setPreviousScore(previousOpt.map(CreditScore::getScoreValue).orElse(null));
        score.setChange(change);
        score.setChangeDirection(changeDirection);
        score.setRetrievedAt(Instant.now());
        score.setStale(false);
        score.setDataQualityScore(craResponse.dataQualityScore());

        CreditScore saved = scoreRepository.save(score);

        // Store factors
        var factors = craResponse.factors().stream().map(f -> {
            var factor = new ScoreFactor();
            factor.setScoreId(saved.getId());
            factor.setCategory(f.category());
            factor.setImpact(f.impact());
            factor.setDirection(f.direction());
            factor.setTitle(f.title());
            factor.setDescription(f.description());
            factor.setWeightingPercent(f.weightingPercent());
            return factor;
        }).toList();
        factorRepository.saveAll(factors);

        // Update cache
        var response = new ScoreResponse(customerId, "EXPERIAN", craResponse.score(), 999,
                saved.getBand(), previousValue, change, changeDirection,
                saved.getRetrievedAt(), false, "cra_live");
        cacheService.putScore(customerId, response);

        // Publish events
        eventPublisher.publishScoreRetrieved(saved);
        if (change != null && change != 0) {
            eventPublisher.publishScoreChanged(saved);
        }

        // Audit log
        logAudit(customerId, "SUCCESS", correlationId, System.currentTimeMillis() - startTime);

        return response;
    }

    private void logAudit(UUID customerId, String status, UUID correlationId, long latencyMs) {
        var audit = new CraApiAuditLog();
        audit.setCustomerId(customerId);
        audit.setProvider("EXPERIAN");
        audit.setRequestHash("N/A");
        audit.setResponseStatus(status);
        audit.setLatencyMs((int) latencyMs);
        audit.setCircuitBreakerState("CLOSED");
        audit.setCorrelationId(correlationId);
        auditLogRepository.save(audit);
    }
}
