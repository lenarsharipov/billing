package com.lenarsharipov.billing.common.exceptions;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String message,
        String code,
        Instant timestamp,
        List<String> details
) {

    public enum Code {
        VALIDATION_ERROR,
        BUSINESS_RULE_VIOLATION,
        INTERNAL_SERVER_ERROR
    }
}
