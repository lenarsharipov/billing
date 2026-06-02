package com.lenarsharipov.billing.usercache.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RedisProfileSubscriptionCache {

    private final SpringDataRedisSubRepository repository;
    private final ObjectMapper objectMapper;

    public RedisProfileSubscriptionCache(
            SpringDataRedisSubRepository repository,
            @Qualifier("cacheObjectMapper") ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public List<SubscriptionDto> get(Long userId) throws Exception {
        var cached = repository.findById(userId);
        if (cached.isPresent()) {
            return objectMapper.readValue(
                    cached.get().getJsonListData(),
                    new TypeReference<>() {
                    }
            );
        }
        return List.of();
    }

    public void put(Long userId, List<SubscriptionDto> list) throws Exception {
        String json = objectMapper.writeValueAsString(list);
        repository.save(new UserSubCacheEntity(userId, json));
    }

    public void evict(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (Exception e) {
            log.error("Не удалось удалить кэш подписок из Redis для userId: {}", userId, e);
        }
    }
}
