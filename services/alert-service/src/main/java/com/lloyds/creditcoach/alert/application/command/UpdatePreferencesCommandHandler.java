package com.lloyds.creditcoach.alert.application.command;

import com.lloyds.creditcoach.alert.application.dto.AlertPreferenceResponse;
import com.lloyds.creditcoach.alert.application.dto.UpdatePreferencesRequest;
import com.lloyds.creditcoach.alert.domain.model.AlertPreference;
import com.lloyds.creditcoach.alert.domain.port.AlertPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdatePreferencesCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdatePreferencesCommandHandler.class);
    private final AlertPreferenceRepository preferenceRepository;

    public UpdatePreferencesCommandHandler(AlertPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional
    public AlertPreferenceResponse handle(UUID customerId, UpdatePreferencesRequest request) {
        log.info("Updating alert preferences for customer: {}", customerId);
        var pref = preferenceRepository.findByCustomerId(customerId).orElseGet(() -> {
            var p = new AlertPreference();
            p.setCustomerId(customerId);
            return p;
        });
        pref.setUtilisationEnabled(request.utilisationEnabled());
        if (request.utilisationThreshold() != null) pref.setUtilisationThreshold(request.utilisationThreshold());
        pref.setPaymentEnabled(request.paymentEnabled());
        pref.setEligibilityEnabled(request.eligibilityEnabled());
        pref.setScoreChangeEnabled(request.scoreChangeEnabled());
        pref.setAllDisabled(request.allDisabled());
        var saved = preferenceRepository.save(pref);
        log.info("Alert preferences updated for customer: {}", customerId);
        return new AlertPreferenceResponse(saved.getId(), saved.getCustomerId(),
                saved.isUtilisationEnabled(), saved.getUtilisationThreshold(),
                saved.isPaymentEnabled(), saved.isEligibilityEnabled(),
                saved.isScoreChangeEnabled(), saved.isAllDisabled());
    }
}
