package com.lenarsharipov.billing.subscription.schedulers;

import com.lenarsharipov.billing.common.utils.DateTimeUtil;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import com.lenarsharipov.billing.subscription.services.SubscriptionDomainService;
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
public class BillingScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionDomainService domainService;
    private final DateTimeUtil dateTimeUtil;

    @Scheduled(cron = "0 0 * * * *")
    @SchedulerLock(
            name = "monthlyBillingLock",
            lockAtMostFor = "45m",
            lockAtLeastFor = "5m"
    )
    public void runMonthlyBilling() {
        Instant now = Instant.now();
        int currentDayOfMonth = dateTimeUtil.getDayOfMonthUtc(now);

        log.info("Запуск регулярного ежемесячного биллинга подписок...");

        List<Subscription> activeSubscriptions =
                subscriptionRepository.findByState(Subscription.State.ACTIVATED);

        List<Subscription> subscriptionsToBill = activeSubscriptions.stream()
                .filter(sub -> dateTimeUtil.getDayOfMonthUtc(
                        sub.getActivationDate()) == currentDayOfMonth
                )
                .filter(sub -> isNotBilledYetToday(sub, now))
                .toList();

        log.info("Найдено {} подписок, готовых к списанию в текущий час.", subscriptionsToBill.size());

        for (Subscription sub : subscriptionsToBill) {
            try {
                domainService.billSubscription(sub);
                log.info("Успешно выставлен счет для подписки ID: {}, userId: {}", sub.getId(), sub.getUserId());
            } catch (Exception e) {
                log.error("Ошибка биллинга для подписки ID: {}: {}", sub.getId(), e.getMessage());
            }
        }
    }

    private boolean isNotBilledYetToday(Subscription sub, Instant now) {
        return sub.getInvoices().stream()
                .noneMatch(invoice -> dateTimeUtil.isSameDayUtc(invoice.getInvoiceDate(), now));
    }
}
