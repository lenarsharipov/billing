package com.lenarsharipov.billing.subscription.dtos;

import java.math.BigDecimal;

public record TariffDto(
        Long id,
        String name,
        BigDecimal amount
) {
}
