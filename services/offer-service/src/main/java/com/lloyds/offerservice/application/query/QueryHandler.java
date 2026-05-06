package com.lloyds.offerservice.application.query;

public interface QueryHandler<Q, R> {
    R handle(Q query);
}
