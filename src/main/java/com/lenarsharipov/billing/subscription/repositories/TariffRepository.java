package com.lenarsharipov.billing.subscription.repositories;

import com.lenarsharipov.billing.subscription.entities.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository
        extends JpaRepository<Tariff, Long> {
}
