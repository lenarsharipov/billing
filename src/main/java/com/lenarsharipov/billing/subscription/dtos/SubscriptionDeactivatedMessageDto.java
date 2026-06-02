package com.lenarsharipov.billing.subscription.dtos;

public record SubscriptionDeactivatedMessageDto(
        Long subscriptionId,
        Long userId
) {}
