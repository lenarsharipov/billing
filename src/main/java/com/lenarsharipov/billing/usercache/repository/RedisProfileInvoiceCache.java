package com.lenarsharipov.billing.usercache.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lenarsharipov.billing.common.constants.CommonConstants.CACHE_USER_INVOICES_KEY_PREFIX;

@Component
@Slf4j
public class RedisProfileInvoiceCache {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisProfileInvoiceCache(
            StringRedisTemplate redisTemplate,
            @Qualifier("cacheObjectMapper") ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Page<InvoiceDto> getPage(Long userId, Pageable pageable) {
        String key = CACHE_USER_INVOICES_KEY_PREFIX + userId;

        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<String> jsonSet = redisTemplate.opsForZSet()
                .reverseRange(key, start, end);

        Long totalCount = redisTemplate.opsForZSet().zCard(key);
        long total = totalCount != null ? totalCount : 0L;

        if (jsonSet == null || jsonSet.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<InvoiceDto> content = jsonSet.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, InvoiceDto.class);
                    } catch (Exception e) {
                        log.error("Ошибка десериализации инвойса из ZSET для userId: {}", userId, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    public void put(Long userId, InvoiceDto invoice) throws Exception {
        String key = CACHE_USER_INVOICES_KEY_PREFIX + userId;
        String json = objectMapper.writeValueAsString(invoice);
        double score = invoice.createdAt().toEpochMilli();
        redisTemplate.opsForZSet()
                .add(key, json, score);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    public boolean exists(Long userId) {
        return redisTemplate.hasKey(CACHE_USER_INVOICES_KEY_PREFIX + userId);
    }

    public void putAll(Long userId, List<InvoiceDto> invoices) {
        invoices.forEach(invoice -> {
            try {
                put(userId, invoice);
            } catch (Exception e) {
                log.error("Ошибка при массовом наполнении кэша инвойсов для userId: {}", userId, e);
            }
        });
    }
}
