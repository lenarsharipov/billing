package com.lenarsharipov.billing.subscription.dtos;

import java.math.BigDecimal;
import java.time.Instant;

public record InvoiceMessageDto(
        Long invoiceId,
        Long userId,
        Instant invoiceDate,
        String tariffName,
        BigDecimal tariffAmount,
        Instant subscriptionActivationDate
) {
}
