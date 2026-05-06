package com.lloyds.creditcoach.alert.application.query;

import com.lloyds.creditcoach.alert.application.dto.AlertPreferenceResponse;
import com.lloyds.creditcoach.alert.domain.model.AlertPreference;
import com.lloyds.creditcoach.alert.domain.port.AlertPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetPreferencesQueryHandler {

    private static final Logger log = LoggerFactory.getLogger(GetPreferencesQueryHandler.class);
    private final AlertPreferenceRepository preferenceRepository;

    public GetPreferencesQueryHandler(AlertPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional(readOnly = true)
    public AlertPreferenceResponse handle(UUID customerId) {
        log.info("Fetching alert preferences for customer: {}", customerId);
        var pref = preferenceRepository.findByCustomerId(customerId)
                .orElseGet(() -> createDefault(customerId));
        return toResponse(pref);
    }

    private AlertPreference createDefault(UUID customerId) {
        var pref = new AlertPreference();
        pref.setCustomerId(customerId);
        return pref;
    }

    private AlertPreferenceResponse toResponse(AlertPreference p) {
        return new AlertPreferenceResponse(p.getId(), p.getCustomerId(),
                p.isUtilisationEnabled(), p.getUtilisationThreshold(),
                p.isPaymentEnabled(), p.isEligibilityEnabled(),
                p.isScoreChangeEnabled(), p.isAllDisabled());
    }
}
