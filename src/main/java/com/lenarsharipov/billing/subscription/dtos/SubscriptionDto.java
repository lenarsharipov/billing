package com.lenarsharipov.billing.subscription.dtos;

import com.lenarsharipov.billing.subscription.entities.Subscription;

import java.time.Instant;

public record SubscriptionDto(
        Long id,
        Long userId,
        TariffDto tariff,
        Subscription.State state,
        Instant activationDate,
        Instant createdAt
) {
}
