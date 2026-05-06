package com.lloyds.offerservice.domain.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, UUID id) {
        super(String.format("%s with id '%s' not found", entityName, id));
    }
}
