package com.lenarsharipov.billing.usercache.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

import static com.lenarsharipov.billing.common.constants.CommonConstants.CACHE_ACTIVE_SUBSCRIPTIONS_KEY_PREFIX;
import static com.lenarsharipov.billing.common.constants.CommonConstants.TWENTY_FOUR_HOURS;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(
        value = CACHE_ACTIVE_SUBSCRIPTIONS_KEY_PREFIX,
        timeToLive = TWENTY_FOUR_HOURS
)
public class UserSubCacheEntity implements Serializable {

    @Id
    private Long userId;

    private String jsonListData; // Храним List<SubscriptionDto> в виде JSON строки
}
