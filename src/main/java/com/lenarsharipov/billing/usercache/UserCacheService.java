package com.lenarsharipov.billing.usercache;

import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDto;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.repositories.InvoiceRepository;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import com.lenarsharipov.billing.usercache.repository.RedisProfileInvoiceCache;
import com.lenarsharipov.billing.usercache.repository.RedisProfileSubscriptionCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final RedisProfileSubscriptionCache redisSubCache;
    private final RedisProfileInvoiceCache redisInvoiceCache;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

    public List<SubscriptionDto> getActiveSubscriptions(Long userId) {
        try {
            List<SubscriptionDto> activeSubs = redisSubCache.get(userId);

            if (activeSubs.isEmpty()) {
                activeSubs = subscriptionRepository.findAllByUserIdAndState(
                                userId, Subscription.State.ACTIVATED
                        ).stream()
                        .map(Subscription::toDto)
                        .toList();

                if (!activeSubs.isEmpty()) {
                    redisSubCache.put(userId, activeSubs);
                }
            }
            return activeSubs;

        } catch (Exception e) {
            log.error("NoSQL БД (Redis) недоступна! Аварийный Fallback на PostgreSQL для подписок userId: {}", userId, e);
            return subscriptionRepository.findAllByUserIdAndState(
                            userId, Subscription.State.ACTIVATED
                    ).stream()
                    .map(Subscription::toDto)
                    .toList();
        }
    }

    public Page<InvoiceDto> getUserInvoices(Long userId, Pageable pageable) {
        try {
            if (!redisInvoiceCache.exists(userId)) {
                List<InvoiceDto> allInvoices =
                        invoiceRepository.findAllByUserId(
                                        userId, Pageable.unpaged()
                                ).stream()
                                .map(Invoice::toDto)
                                .toList();
                redisInvoiceCache.putAll(userId, allInvoices);
            }

            return redisInvoiceCache.getPage(userId, pageable);

        } catch (Exception e) {
            log.error("NoSQL БД (Redis) недоступна! Аварийный Fallback на PostgreSQL для инвойсов userId: {}", userId, e);
            return invoiceRepository.findAllByUserId(userId, pageable)
                    .map(Invoice::toDto);
        }
    }
}