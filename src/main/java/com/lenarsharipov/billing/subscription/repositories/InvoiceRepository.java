package com.lenarsharipov.billing.subscription.repositories;

import com.lenarsharipov.billing.subscription.entities.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository
        extends JpaRepository<Invoice, Long> {

    Page<Invoice> findAllByUserId(Long userId, Pageable pageable);
}
