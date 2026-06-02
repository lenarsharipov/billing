package com.lenarsharipov.billing.usercache.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@NoArgsConstructor
@RedisHash(value = "cache:active_subs", timeToLive = 86400)
public class UserSubCacheEntity implements Serializable {

    @Id
    private Long userId;

    private String jsonListData; // Храним List<SubscriptionDto> в виде JSON строки
}
