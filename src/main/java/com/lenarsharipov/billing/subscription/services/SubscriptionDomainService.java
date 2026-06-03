package com.lenarsharipov.billing.subscription.services;

import com.lenarsharipov.billing.common.repositories.OutboxEventRepository;
import com.lenarsharipov.billing.common.utils.DateTimeUtil;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.entities.Tariff;
import com.lenarsharipov.billing.subscription.factories.SubscriptionBillingFactory;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionDomainService {

    private final SubscriptionRepository subscriptionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final DateTimeUtil dateTimeUtil;
    private final SubscriptionBillingFactory factory;

    @Transactional
    public Invoice createNewSubscription(
            Long userId,
            Tariff tariff,
            LocalDate date
    ) {
        var activationDate = dateTimeUtil.toStartOfDayUtc(date);
        var subscription = factory.buildActivatedSubscription(
                userId, tariff, activationDate
        );

        var invoice = factory.buildInvoice(userId, activationDate);
        subscription.addInvoice(invoice);

        subscriptionRepository.save(subscription);
        var outboxEvent = factory.buildInvoiceOutboxEvent(invoice, subscription);
        outboxEventRepository.save(outboxEvent);

        return invoice;
    }

    @Transactional
    public void billSubscription(Subscription subscription) {
        var now = dateTimeUtil.getCurrentInstant();
        Invoice invoice = factory.buildInvoice(subscription.getUserId(), now);
        subscription.addInvoice(invoice);
        subscriptionRepository.save(subscription);
        var outboxEvent = factory.buildInvoiceOutboxEvent(invoice, subscription);
        outboxEventRepository.save(outboxEvent);
    }

    @Transactional
    public void deactivateSubscription(Subscription subscription) {
        subscription.setState(Subscription.State.DEACTIVATED);
        subscriptionRepository.save(subscription);
        var outboxEvent = factory.buildDeactivationOutboxEvent(subscription);
        outboxEventRepository.save(outboxEvent);
    }
}
