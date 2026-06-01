package com.lenarsharipov.billing.common.ui.reqs;

import jakarta.validation.constraints.NotNull;

public record SubscriptionDeactivationRequest(

        @NotNull(message = "Не может быть пустым или null")
        Long tariffId
) {
}
