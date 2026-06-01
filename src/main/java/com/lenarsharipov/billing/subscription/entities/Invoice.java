package com.lenarsharipov.billing.subscription.entities;

import com.lenarsharipov.billing.common.entities.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "invoices")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Invoice extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    private Instant invoiceDate;
}
