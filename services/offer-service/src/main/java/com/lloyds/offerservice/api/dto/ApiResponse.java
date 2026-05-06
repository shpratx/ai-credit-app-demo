package com.lloyds.offerservice.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(T data, Meta meta) {

    public record Meta(String requestId, Instant timestamp, Pagination pagination) {}

    public record Pagination(int page, int size, long total, int totalPages) {}

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, new Meta(MDC.get("correlationId"), Instant.now(), null));
    }

    public static <T> ApiResponse<List<T>> paginated(List<T> data, int page, int size, long total, int totalPages) {
        return new ApiResponse<>(data, new Meta(MDC.get("correlationId"), Instant.now(), new Pagination(page, size, total, totalPages)));
    }
}
