package com.lenarsharipov.billing.subscription.validators;

import com.lenarsharipov.billing.common.exceptions.SubscriptionBusinessRuleException;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.entities.Tariff;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SubscriptionBusinessValidator {

    public void validateForActivation(
            boolean hasActiveSubscription,
            Tariff tariff,
            LocalDate activationDate
    ) {
        if (hasActiveSubscription) {
            throw new SubscriptionBusinessRuleException("У пользователя уже есть активная подписка.");
        }

        if (tariff == null) {
            throw new SubscriptionBusinessRuleException("Выбранный тариф подписки не найден в системе.");
        }

        if (activationDate.isBefore(LocalDate.now())) {
            throw new SubscriptionBusinessRuleException("Выбранная дата активации не может быть в прошлом.");
        }
    }

    public void validateForDeactivation(Subscription subscription) {
        if (subscription == null) {
            throw new SubscriptionBusinessRuleException(
                    "У пользователя не найдено активной подписки с указанным типом тарифа для деактивации."
            );
        }
    }
}
