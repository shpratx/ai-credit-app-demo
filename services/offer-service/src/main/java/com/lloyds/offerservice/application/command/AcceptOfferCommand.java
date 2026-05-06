package com.lloyds.offerservice.application.command;

import java.util.UUID;

public record AcceptOfferCommand(UUID offerId, UUID customerId) {}
