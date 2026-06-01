package com.lenarsharipov.billing.subscription.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.common.entities.OutboxEvent;
import com.lenarsharipov.billing.common.mappers.DateTimeMapper;
import com.lenarsharipov.billing.common.repositories.OutboxEventRepository;
import com.lenarsharipov.billing.subscription.dtos.InvoiceMessageDto;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.entities.Tariff;
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
    private final DateTimeMapper dateTimeMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Invoice createNewSubscription(
            Long userId,
            Tariff tariff,
            LocalDate date
    ) {
        var activationDate = dateTimeMapper.toStartOfDayUtc(date);

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .tariff(tariff)
                .state(Subscription.State.ACTIVATED)
                .activationDate(activationDate)
                .build();

        Invoice invoice = Invoice.builder()
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
                .aggregateType(OutboxEvent.AggregateType.INVOICE.name())
                .aggregateId(invoice.getId().toString())
                .payload(jsonPayload)
                .build();
        outboxEventRepository.save(outboxEvent);

        return invoice;
    }
}
