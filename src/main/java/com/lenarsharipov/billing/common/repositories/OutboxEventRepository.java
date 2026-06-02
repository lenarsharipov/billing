package com.lenarsharipov.billing.common.repositories;

import com.lenarsharipov.billing.common.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    @Query(
            "SELECT o " +
            "FROM OutboxEvent o " +
            "WHERE o.status = :status " +
            "AND o.nextAttemptAt <= :now " +
            "ORDER BY o.createdAt ASC"
    )
    List<OutboxEvent> findReadyForRetry(
            @Param("now") Instant now,
            @Param("status") OutboxEvent.Status status
    );

    @Modifying
    @Query(
            "UPDATE OutboxEvent o " +
            "SET o.status = :status " +
            "WHERE o.aggregateId = :aggregateId"
    )
    void updateStatusByAggregateId(
            @Param("aggregateId") String aggregateId,
            @Param("status") OutboxEvent.Status status
    );

    Optional<OutboxEvent> findByAggregateTypeAndAggregateId(
            String aggregateType,
            String aggregateId
    );
}
