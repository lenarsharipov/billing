package com.lenarsharipov.billing.subscription.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.common.entities.OutboxEvent;
import com.lenarsharipov.billing.common.repositories.OutboxEventRepository;
import com.lenarsharipov.billing.common.utils.DateTimeUtil;
import com.lenarsharipov.billing.subscription.dtos.InvoiceMessageDto;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.entities.Tariff;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

import static com.lenarsharipov.billing.common.entities.OutboxEvent.AggregateType.INVOICE;

@Service
@RequiredArgsConstructor
public class SubscriptionDomainService {

    private final SubscriptionRepository subscriptionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final DateTimeUtil dateTimeUtil;
    private final ObjectMapper objectMapper;

    @Transactional
    public Invoice createNewSubscription(
            Long userId,
            Tariff tariff,
            LocalDate date
    ) {
        var activationDate = dateTimeUtil.toStartOfDayUtc(date);

        var subscription = Subscription.builder()
                .userId(userId)
                .tariff(tariff)
                .state(Subscription.State.ACTIVATED)
                .activationDate(activationDate)
                .build();

        var invoice = Invoice.builder()
                .userId(userId)
                .invoiceDate(activationDate)
                .build();
        subscription.addInvoice(invoice);

        subscriptionRepository.save(subscription);

        String jsonPayload;
        try {
            var messageDto = new InvoiceMessageDto(
                    invoice.getId(),
                    userId,
                    activationDate,
                    tariff.getName(),
                    tariff.getAmount(),
                    activationDate
            );
            jsonPayload = objectMapper.writeValueAsString(messageDto);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации инвойса в JSON", e);
        }

        var outboxEvent = OutboxEvent.builder()
                .aggregateType(INVOICE.name())
                .aggregateId(invoice.getId().toString())
                .payload(jsonPayload)
                .build();
        outboxEventRepository.save(outboxEvent);

        return invoice;
    }

    @Transactional
    public void billSubscription(Subscription subscription) {
        var now = Instant.now();
        var tariff = subscription.getTariff();

        var invoice = Invoice.builder()
                .userId(subscription.getUserId())
                .invoiceDate(now)
                .build();

        subscription.addInvoice(invoice);

        subscriptionRepository.save(subscription);

        String jsonPayload;
        try {
            var messageDto = new InvoiceMessageDto(
                    invoice.getId(),
                    subscription.getUserId(),
                    now,
                    tariff.getName(),
                    tariff.getAmount(),
                    subscription.getActivationDate()
            );
            jsonPayload = objectMapper.writeValueAsString(messageDto);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации регулярного инвойса для подписки: " + subscription.getId(), e);
        }

        var outboxEvent = OutboxEvent.builder()
                .aggregateType(OutboxEvent.AggregateType.INVOICE.name())
                .aggregateId(invoice.getId().toString())
                .payload(jsonPayload)
                .build();
        outboxEventRepository.save(outboxEvent);
    }

    @Transactional
    public void deactivateSubscription(Subscription subscription) {
        subscription.setState(Subscription.State.DEACTIVATED);
        subscriptionRepository.save(subscription);

        String jsonPayload;
        try {
            var messageDto = new com.lenarsharipov.billing.subscription.dtos.SubscriptionDeactivatedMessageDto(
                    subscription.getId(),
                    subscription.getUserId()
            );
            jsonPayload = objectMapper.writeValueAsString(messageDto);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации события деактивации подписки в JSON", e);
        }

        var outboxEvent = OutboxEvent.builder()
                .aggregateType(OutboxEvent.AggregateType.SUBSCRIPTION_DEACTIVATED.name())
                .aggregateId(subscription.getId().toString())
                .payload(jsonPayload)
                .build();
        outboxEventRepository.save(outboxEvent);
    }
}
