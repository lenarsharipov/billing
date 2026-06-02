package com.lenarsharipov.billing.usercache.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataRedisSubRepository
        extends CrudRepository<UserSubCacheEntity, Long> {
}
