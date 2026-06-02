package com.lenarsharipov.billing.subscription.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.common.entities.OutboxEvent;
import com.lenarsharipov.billing.subscription.dtos.InvoiceMessageDto;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDeactivatedMessageDto;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.entities.Tariff;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.lenarsharipov.billing.common.constants.CommonConstants.ROUTING_INVOICE_CREATED;
import static com.lenarsharipov.billing.common.constants.CommonConstants.ROUTING_SUB_DEACTIVATED;

@Component
@RequiredArgsConstructor
public class SubscriptionBillingFactory {

    private final ObjectMapper objectMapper;

    public Subscription buildActivatedSubscription(Long userId, Tariff tariff, Instant activationDate) {
        return Subscription.builder()
                .userId(userId)
                .tariff(tariff)
                .state(Subscription.State.ACTIVATED)
                .activationDate(activationDate)
                .build();
    }

    public Invoice buildInvoice(Long userId, Instant invoiceDate) {
        return Invoice.builder()
                .userId(userId)
                .invoiceDate(invoiceDate)
                .build();
    }

    public OutboxEvent buildInvoiceOutboxEvent(Invoice invoice, Subscription subscription) {
        Tariff tariff = subscription.getTariff();
        try {
            var messageDto = new InvoiceMessageDto(
                    invoice.getId(),
                    invoice.getUserId(),
                    invoice.getInvoiceDate(),
                    tariff.getName(),
                    tariff.getAmount(),
                    subscription.getActivationDate()
            );

            return OutboxEvent.builder()
                    .aggregateType(OutboxEvent.AggregateType.INVOICE.name())
                    .aggregateId(invoice.getId().toString())
                    .routingKey(ROUTING_INVOICE_CREATED)
                    .payload(objectMapper.writeValueAsString(messageDto))
                    .status(OutboxEvent.Status.PENDING)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации инвойса в JSON для ID: " + invoice.getId(), e);
        }
    }

    public OutboxEvent buildDeactivationOutboxEvent(Subscription subscription) {
        try {
            var messageDto = new SubscriptionDeactivatedMessageDto(
                    subscription.getId(),
                    subscription.getUserId()
            );

            return OutboxEvent.builder()
                    .aggregateType(OutboxEvent.AggregateType.SUBSCRIPTION.name())
                    .aggregateId(subscription.getId().toString())
                    .routingKey(ROUTING_SUB_DEACTIVATED)
                    .payload(objectMapper.writeValueAsString(messageDto))
                    .status(OutboxEvent.Status.PENDING)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации события деактивации в JSON для подписки: " + subscription.getId(), e);
        }
    }
}
