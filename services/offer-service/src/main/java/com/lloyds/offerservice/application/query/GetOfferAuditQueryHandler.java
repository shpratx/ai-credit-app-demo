package com.lloyds.offerservice.application.query;

import com.lloyds.offerservice.domain.model.OfferAuditEntry;
import com.lloyds.offerservice.domain.port.OfferAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetOfferAuditQueryHandler implements QueryHandler<GetOfferAuditQuery, List<OfferAuditEntry>> {

    private static final Logger log = LoggerFactory.getLogger(GetOfferAuditQueryHandler.class);
    private final OfferAuditRepository auditRepository;

    public GetOfferAuditQueryHandler(OfferAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferAuditEntry> handle(GetOfferAuditQuery query) {
        log.info("Fetching offer audit trail for customer: {}", query.customerId());
        return auditRepository.findByCustomerId(query.customerId());
    }
}
