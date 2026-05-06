package com.lloyds.offerservice.application.query;

import com.lloyds.offerservice.application.dto.SecciDocument;
import com.lloyds.offerservice.domain.exception.EntityNotFoundException;
import com.lloyds.offerservice.domain.model.PreApprovedOffer;
import com.lloyds.offerservice.domain.port.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * CCA s.55A: generates SECCI document with all prescribed fields.
 */
@Service
public class GetSecciQueryHandler implements QueryHandler<GetSecciQuery, SecciDocument> {

    private static final Logger log = LoggerFactory.getLogger(GetSecciQueryHandler.class);
    private final OfferRepository offerRepository;

    public GetSecciQueryHandler(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SecciDocument handle(GetSecciQuery query) {
        log.info("Generating SECCI document for offer: {}", query.offerId());

        PreApprovedOffer offer = offerRepository.findById(query.offerId())
                .orElseThrow(() -> new EntityNotFoundException("PreApprovedOffer", query.offerId()));

        return new SecciDocument(
                offer.getId(),
                "Lloyds Bank plc",
                "25 Gresham Street, London, EC2V 7HN",
                "0345 300 0000",
                "creditcoach@lloydsbanking.com",
                "Personal Loan",
                offer.getAmount(),
                "Funds transferred to nominated account within 24 hours of acceptance",
                offer.getTerm(),
                String.format("Monthly repayments of £%s over %d months", offer.getMonthlyPayment(), offer.getTerm()),
                offer.getTotalPayable(),
                offer.getRate(),
                offer.getApr(),
                true,
                offer.getTotalChargeForCredit(),
                "You have the right to withdraw within 14 days of agreement without giving a reason (CCA s.66A)",
                14,
                BigDecimal.ZERO, // CCA s.95A: 0% early repayment for Lloyds
                new BigDecimal("12.00"), // CONC 7.7.5R: late fee capped at £12
                "Financial Conduct Authority (FCA)",
                "Lloyds Banking Group, PO Box 1000, Andover, BX1 1LT");
    }
}
