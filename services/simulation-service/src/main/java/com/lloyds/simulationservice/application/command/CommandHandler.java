package com.lloyds.simulationservice.application.command;

public interface CommandHandler<C, R> {
    R handle(C command);
}
