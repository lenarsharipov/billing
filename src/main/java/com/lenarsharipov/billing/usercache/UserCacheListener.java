package com.lenarsharipov.billing.usercache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import com.lenarsharipov.billing.subscription.dtos.InvoiceMessageDto;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDeactivatedMessageDto;
import com.lenarsharipov.billing.usercache.repository.RedisProfileInvoiceCache;
import com.lenarsharipov.billing.usercache.repository.RedisProfileSubscriptionCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.lenarsharipov.billing.common.constants.CommonConstants.DEACTIVATED_QUEUE;
import static com.lenarsharipov.billing.common.constants.CommonConstants.INVOICE_QUEUE;

@Slf4j
@Component
public class UserCacheListener {

    private final RedisProfileSubscriptionCache redisSubCache;
    private final RedisProfileInvoiceCache redisInvoiceCache;
    private final ObjectMapper cacheObjectMapper;

    public UserCacheListener(
            RedisProfileSubscriptionCache redisSubCache,
            RedisProfileInvoiceCache redisInvoiceCache,
            @Qualifier("cacheObjectMapper") ObjectMapper cacheObjectMapper
    ) {
        this.redisSubCache = redisSubCache;
        this.redisInvoiceCache = redisInvoiceCache;

        this.cacheObjectMapper = cacheObjectMapper;
    }

    @RabbitListener(queues = INVOICE_QUEUE)
    public void handleInvoiceCreatedEvent(String messageJson) {
        try {
            InvoiceMessageDto event = cacheObjectMapper.readValue(messageJson, InvoiceMessageDto.class);
            log.info("Событие брокера: получен инвойс {} для актуализации NoSQL", event.invoiceId());

            var invoiceDto = new InvoiceDto(
                    event.invoiceId(),
                    event.userId(),
                    event.invoiceDate(),
                    Instant.now()
            );
            redisInvoiceCache.put(event.userId(), invoiceDto);
            redisSubCache.evict(event.userId());
        } catch (Exception e) {
            log.error("Ошибка актуализации NoSQL кэша по событию инвойса", e);
        }
    }

    @RabbitListener(queues = DEACTIVATED_QUEUE)
    public void handleSubscriptionDeactivatedEvent(String messageJson) {
        try {
            SubscriptionDeactivatedMessageDto event =
                    cacheObjectMapper.readValue(messageJson, SubscriptionDeactivatedMessageDto.class);
            log.info("NoSQL Кэш поймал событие деактивации подписки пользователя: {}", event.userId());

            redisSubCache.evict(event.userId());
        } catch (Exception e) {
            log.error("Ошибка асинхронной инвалидации подписок в NoSQL", e);
        }
    }
}