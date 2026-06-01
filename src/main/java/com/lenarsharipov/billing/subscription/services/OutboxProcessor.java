package com.lenarsharipov.billing.subscription.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.common.entities.OutboxEvent;
import com.lenarsharipov.billing.common.repositories.OutboxEventRepository;
import com.lenarsharipov.billing.subscription.dtos.InvoiceMessageDto;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void publishImmediate(Invoice invoice) {
        String jsonPayload;
        try {
            InvoiceMessageDto messageDto = new InvoiceMessageDto(
                    invoice.getId(), invoice.getUserId(), invoice.getInvoiceDate(),
                    invoice.getSubscription().getTariff().getName(),
                    invoice.getSubscription().getTariff().getAmount(),
                    invoice.getSubscription().getActivationDate()
            );
            jsonPayload = objectMapper.writeValueAsString(messageDto);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        rabbitTemplate.convertAndSend("subscription.events", "invoice.created", jsonPayload);
        outboxEventRepository.updateStatusByAggregateId(invoice.getId().toString(), OutboxEvent.Status.SENT);
    }

    @Transactional
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 3.0)
    )
    public void processScheduledEvent(OutboxEvent event) {
        event.setAttempts(event.getAttempts() + 1);
        rabbitTemplate.convertAndSend("subscription.events", "invoice.created", event.getPayload());

        event.setStatus(OutboxEvent.Status.SENT);
        outboxEventRepository.save(event);
    }

    @Recover
    @Transactional
    public void recover(Exception e, OutboxEvent event) {
        log.error("!!! Инвойс {} окончательно превысил лимит попыток. Перемещен в статус DB-DLQ !!!", event.getAggregateId());
        event.setStatus(OutboxEvent.Status.DLQ);
        outboxEventRepository.save(event);
    }
}
