package com.lenarsharipov.billing.subscription.schedulers;

import com.lenarsharipov.billing.common.entities.OutboxEvent;
import com.lenarsharipov.billing.common.repositories.OutboxEventRepository;
import com.lenarsharipov.billing.subscription.services.OutboxProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxProcessor outboxProcessor;

    @Scheduled(fixedDelay = 60000)
    @SchedulerLock(
            name = "outboxRetryLock",
            lockAtMostFor = "10m",
            lockAtLeastFor = "10s"
    )
    public void retryPublishing() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findReadyForRetry(
                Instant.now(), OutboxEvent.Status.PENDING
        );

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info(
                "ShedLock захвачен репликой. Найдено {} инвойсов для досылки.",
                pendingEvents.size()
        );

        for (OutboxEvent event : pendingEvents) {
            try {
                outboxProcessor.processScheduledEvent(event);
            } catch (Exception e) {
                log.error(
                        "Критический сбой инфраструктуры СУБД для ивента {}. Прерываем цикл.",
                        event.getAggregateId(),
                        e
                );
                break;
            }
        }
    }
}
