package com.lloyds.offerservice.application.command;

public interface CommandHandler<C, R> {
    R handle(C command);
}
