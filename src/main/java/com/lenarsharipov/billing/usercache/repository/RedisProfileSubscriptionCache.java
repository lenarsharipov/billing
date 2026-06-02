package com.lenarsharipov.billing.usercache.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisProfileSubscriptionCache {

    private final SpringDataRedisSubRepository repository;

    @Qualifier("cacheObjectMapper")
    private final ObjectMapper objectMapper;

    public List<SubscriptionDto> get(Long userId) throws Exception {
        var cached = repository.findById(userId);
        if (cached.isPresent()) {
            return objectMapper.readValue(cached.get().getJsonListData(), new TypeReference<>() {
            });
        }
        return List.of();
    }

    public void evict(Long userId) {
        repository.deleteById(userId);
    }
}
