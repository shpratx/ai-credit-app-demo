package com.lloyds.simulationservice.application.query;

public interface QueryHandler<Q, R> {
    R handle(Q query);
}
