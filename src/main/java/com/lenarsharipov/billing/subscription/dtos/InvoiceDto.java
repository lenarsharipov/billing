package com.lenarsharipov.billing.subscription.dtos;

import java.time.Instant;

public record InvoiceDto(
        Long id,
        Long userId,
        Instant invoiceDate,
        Instant createdAt
) {
}
