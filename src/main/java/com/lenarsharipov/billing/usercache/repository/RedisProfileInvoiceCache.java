package com.lenarsharipov.billing.usercache.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class RedisProfileInvoiceCache {

    private static final String KEY_PREFIX = "cache:user_invoices:";
    private final StringRedisTemplate redisTemplate;
    @Qualifier("cacheObjectMapper") private final ObjectMapper objectMapper;

    public Page<InvoiceDto> getPage(Long userId, Pageable pageable) {
        String key = KEY_PREFIX + userId;
        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<String> jsonSet = redisTemplate.opsForZSet().reverseRange(key, start, end);
        Long totalCount = redisTemplate.opsForZSet().zCard(key);
        long total = totalCount != null ? totalCount : 0L;

        if (jsonSet == null || jsonSet.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<InvoiceDto> content = jsonSet.stream()
                .map(json -> {
                    try { return objectMapper.readValue(json, InvoiceDto.class); }
                    catch (Exception e) { return null; }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    public void put(Long userId, InvoiceDto invoice) throws Exception {
        String json = objectMapper.writeValueAsString(invoice);
        double score = invoice.createdAt().toEpochMilli();
        redisTemplate.opsForZSet().add(KEY_PREFIX + userId, json, score);
        redisTemplate.expire(KEY_PREFIX + userId, 24, TimeUnit.HOURS);
    }
}
