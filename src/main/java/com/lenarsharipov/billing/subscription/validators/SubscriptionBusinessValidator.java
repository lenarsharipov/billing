package com.lenarsharipov.billing.subscription.validators;

import com.lenarsharipov.billing.common.exceptions.SubscriptionBusinessException;
import com.lenarsharipov.billing.subscription.entities.Tariff;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionBusinessValidator {

    public void validateForActivation(
            boolean hasActiveSubscription,
            Tariff tariff
    ) {
        if (hasActiveSubscription) {
            throw new SubscriptionBusinessException("У пользователя уже есть активная подписка.");
        }

        if (tariff == null) {
            throw new SubscriptionBusinessException("Выбранный тариф подписки не найден в системе.");
        }
    }
}
