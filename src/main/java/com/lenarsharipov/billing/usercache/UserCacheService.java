package com.lenarsharipov.billing.usercache;

import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDto;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.repositories.InvoiceRepository;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import com.lenarsharipov.billing.usercache.dto.UserProfileResponse;
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

    public UserProfileResponse getUserProfile(Long userId, Pageable pageable) {
        try {
            List<SubscriptionDto> activeSubs = redisSubCache.get(userId);

            Page<InvoiceDto> invoicesPage = redisInvoiceCache.getPage(userId, pageable);

            log.info("Профиль пользователя {} успешно собран из NoSQL БД", userId);
            return new UserProfileResponse(activeSubs, invoicesPage);

        } catch (Exception e) {
            log.error("NoSQL БД (Redis) недоступна! Аварийное переключение на PostgreSQL для userId: {}", userId, e);
            List<SubscriptionDto> dbSubs =
                    subscriptionRepository.findAllByUserIdAndState(
                            userId,
                            Subscription.State.ACTIVATED
                    ).stream()
                            .map(Subscription::toDto)
                            .toList();

            Page<Invoice> dbInvoicesPage = invoiceRepository.findAllByUserId(userId, pageable);
            Page<InvoiceDto> dbInvoicesDtoPage = dbInvoicesPage.map(Invoice::toDto);

            return new UserProfileResponse(dbSubs, dbInvoicesDtoPage);
        }
    }
}