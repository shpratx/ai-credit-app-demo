package com.lloyds.creditcoach.conversation.api.controller;

import com.lloyds.creditcoach.conversation.application.command.SendConversationalQueryHandler;
import com.lloyds.creditcoach.conversation.application.dto.ConversationRequest;
import com.lloyds.creditcoach.conversation.application.dto.ConversationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credit-coach/conversations")
public class ConversationController {

    private final SendConversationalQueryHandler handler;

    public ConversationController(SendConversationalQueryHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> sendQuery(@Valid @RequestBody ConversationRequest request) {
        return ResponseEntity.ok(handler.handle(request));
    }
}
