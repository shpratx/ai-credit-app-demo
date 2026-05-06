package com.lloyds.simulationservice.domain.exception;

public class SimulationTimeoutException extends RuntimeException {
    public SimulationTimeoutException(String message) {
        super(message);
    }
}
