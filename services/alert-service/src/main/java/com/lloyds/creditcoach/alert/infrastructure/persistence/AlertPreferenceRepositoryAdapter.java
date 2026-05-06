package com.lloyds.creditcoach.alert.infrastructure.persistence;

import com.lloyds.creditcoach.alert.domain.model.AlertPreference;
import com.lloyds.creditcoach.alert.domain.port.AlertPreferenceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AlertPreferenceRepositoryAdapter implements AlertPreferenceRepository {

    private final JpaAlertPreferenceRepository jpaRepository;

    public AlertPreferenceRepositoryAdapter(JpaAlertPreferenceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<AlertPreference> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).map(this::toDomain);
    }

    @Override
    public AlertPreference save(AlertPreference pref) {
        var entity = toEntity(pref);
        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private AlertPreference toDomain(AlertPreferenceEntity e) {
        var p = new AlertPreference();
        p.setId(e.getId());
        p.setCustomerId(e.getCustomerId());
        p.setUtilisationEnabled(e.isUtilisationEnabled());
        p.setUtilisationThreshold(e.getUtilisationThreshold());
        p.setPaymentEnabled(e.isPaymentEnabled());
        p.setEligibilityEnabled(e.isEligibilityEnabled());
        p.setScoreChangeEnabled(e.isScoreChangeEnabled());
        p.setAllDisabled(e.isAllDisabled());
        return p;
    }

    private AlertPreferenceEntity toEntity(AlertPreference p) {
        var e = new AlertPreferenceEntity();
        e.setId(p.getId());
        e.setCustomerId(p.getCustomerId());
        e.setUtilisationEnabled(p.isUtilisationEnabled());
        e.setUtilisationThreshold(p.getUtilisationThreshold());
        e.setPaymentEnabled(p.isPaymentEnabled());
        e.setEligibilityEnabled(p.isEligibilityEnabled());
        e.setScoreChangeEnabled(p.isScoreChangeEnabled());
        e.setAllDisabled(p.isAllDisabled());
        return e;
    }
}
