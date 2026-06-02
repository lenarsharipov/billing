package com.lenarsharipov.billing.usercache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import com.lenarsharipov.billing.subscription.dtos.InvoiceMessageDto;
import com.lenarsharipov.billing.usercache.repository.RedisProfileInvoiceCache;
import com.lenarsharipov.billing.usercache.repository.RedisProfileSubscriptionCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCacheListener {

    private final RedisProfileSubscriptionCache redisSubCache;
    private final RedisProfileInvoiceCache redisInvoiceCache;

    @Qualifier("cacheObjectMapper")
    private final ObjectMapper cacheObjectMapper;

    // 1. Актуализация инвойсов и подписки при создании нового счета
    @RabbitListener(queues = "${app.rabbitmq.invoice-queue}")
    public void handleInvoiceCreatedEvent(String messageJson) {
        try {
            InvoiceMessageDto event = cacheObjectMapper.readValue(messageJson, InvoiceMessageDto.class);
            log.info("Событие брокера: получен инвойс {} для актуализации NoSQL", event.invoiceId());

            // Собираем InvoiceDto и точечно пушим в ZSET инвойсов пользователя
            InvoiceDto invoiceDto = new InvoiceDto(
                    event.invoiceId(),
                    event.userId(),
                    event.invoiceDate(),
                    Instant.now() // В реальном событии берем createdAt из сообщения
            );
            redisInvoiceCache.put(event.userId(), invoiceDto);

            // Так как пришел инвойс, подписка гарантированно активна.
            // Инвалидируем кэш подписок пользователя, чтобы при следующем GET-запросе он обновился из БД
            redisSubCache.evict(event.userId());

        } catch (Exception e) {
            log.error("Ошибка актуализации NoSQL кэша по событию инвойса", e);
        }
    }

    // 2. Актуализация при деактивации подписки
    @RabbitListener(queues = "${app.rabbitmq.subscription-deactivated-queue}")
    public void handleSubscriptionDeactivatedEvent(Long userId) {
        try {
            log.info("Событие брокера: подписка пользователя {} деактивирована.", userId);
            // Просто стираем кэш активных подписок в Redis.
            // При следующем GET контроллер увидит пустой список.
            redisSubCache.evict(userId);
        } catch (Exception e) {
            log.error("Ошибка инвалидации NoSQL кэша при деактивации", e);
        }
    }
}