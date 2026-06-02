package com.lenarsharipov.billing.subscription.entities;

import com.lenarsharipov.billing.common.entities.BaseAuditEntity;
import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
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

    public InvoiceDto toDto() {
        return new InvoiceDto(
                this.id,
                this.userId,
                this.invoiceDate,
                this.getCreatedAt()
        );
    }
}
