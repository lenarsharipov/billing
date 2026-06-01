package com.lenarsharipov.billing.subscription.dtos;

import java.math.BigDecimal;
import java.time.Instant;

public record InvoiceMessageDto(
        Long invoiceId,
        Long userId,
        Instant issueDate,
        String tariffName,
        BigDecimal amount,
        Instant subscriptionActivatedAt
) {
}
