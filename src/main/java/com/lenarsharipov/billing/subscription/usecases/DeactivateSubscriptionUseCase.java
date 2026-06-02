package com.lenarsharipov.billing.subscription.usecases;

import com.lenarsharipov.billing.common.ui.reqs.SubscriptionDeactivationRequest;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import com.lenarsharipov.billing.subscription.services.OutboxProcessor;
import com.lenarsharipov.billing.subscription.services.SubscriptionDomainService;
import com.lenarsharipov.billing.subscription.validators.SubscriptionBusinessValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeactivateSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionBusinessValidator validator;
    private final SubscriptionDomainService domainService;
    private final OutboxProcessor outboxProcessor;

    public void execute(Long userId, SubscriptionDeactivationRequest request) {
        log.info("Запуск деактивации подписки для userId: {}, tariffId: {}", userId, request.tariffId());

        Subscription subscription = subscriptionRepository.findByUserIdAndTariffIdAndState(
                userId, request.tariffId(), Subscription.State.ACTIVATED
        ).orElse(null);

        validator.validateForDeactivation(subscription);

        domainService.deactivateSubscription(subscription);

        try {
            outboxProcessor.publishImmediateDeactivation(subscription.getId());
        } catch (Exception e) {
            log.error(
                    "RabbitMQ недоступен при деактивации подписки {}. Сработает фоновый планировщик. Ошибка: {}",
                    subscription.getId(),
                    e.getMessage()
            );
        }
    }
}
