package com.lenarsharipov.billing.subscription.repositories;

import com.lenarsharipov.billing.subscription.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    boolean existsByUserIdAndState(Long userId, Subscription.State state);

    List<Subscription> findByState(Subscription.State state);

    List<Subscription> findAllByUserIdAndState(Long userId, Subscription.State state);

    Optional<Subscription> findByUserIdAndTariffIdAndState(
            Long userId,
            Long tariffId,
            Subscription.State state
    );
}
