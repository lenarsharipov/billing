package com.lenarsharipov.billing.subscription.usecases;

import com.lenarsharipov.billing.common.ui.reqs.SubscriptionActivationRequest;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import com.lenarsharipov.billing.subscription.repositories.TariffRepository;
import com.lenarsharipov.billing.subscription.services.OutboxProcessor;
import com.lenarsharipov.billing.subscription.services.SubscriptionDomainService;
import com.lenarsharipov.billing.subscription.validators.SubscriptionBusinessValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivateSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final TariffRepository tariffRepository;
    private final SubscriptionBusinessValidator validator;
    private final SubscriptionDomainService domainService;
    private final OutboxProcessor outboxProcessor;

    public void execute(Long userId, SubscriptionActivationRequest request) {
        var hasActiveSubscription =
                subscriptionRepository.existsByUserIdAndState(userId, Subscription.State.ACTIVATED);
        var tariff = tariffRepository.findById(request.tariffId()).orElse(null);
        validator.validateForActivation(hasActiveSubscription, tariff);
        var invoice = domainService.createNewSubscription(userId, tariff, request.activationDate());

        try {
//            outboxProcessor
        } catch (Exception e) {
            log.error("RabbitMQ недоступен при первичной отправке инвойса {}. " +
                    "Систему подстрахует Outbox Scheduler. Ошибка: {}", invoice.getId(), e.getMessage());
        }
    }
}
