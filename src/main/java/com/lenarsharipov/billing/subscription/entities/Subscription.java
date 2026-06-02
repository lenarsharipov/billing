package com.lenarsharipov.billing.subscription.entities;

import com.lenarsharipov.billing.common.entities.BaseAuditEntity;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDto;
import com.lenarsharipov.billing.subscription.dtos.TariffDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subscriptions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Subscription extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    private Long userId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private State state = State.ACTIVATED;

    private Instant activationDate;

    @OneToMany(
            mappedBy = "subscription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Invoice> invoices = new ArrayList<>();

    public enum State {
        ACTIVATED,
        DEACTIVATED
    }

    public SubscriptionDto toDto() {
        TariffDto tariffDto = null;
        if (this.tariff != null) {
            tariffDto = new TariffDto(
                    this.tariff.getId(),
                    this.tariff.getName(),
                    this.tariff.getAmount()
            );
        }

        return new SubscriptionDto(
                this.id,
                this.userId,
                tariffDto,
                this.state,
                this.activationDate,
                this.getCreatedAt()
        );
    }

    /**
     * Хелпер-метод для добавления инвойса.
     * Синхронизирует обе стороны связи в памяти.
     */
    public void addInvoice(Invoice invoice) {
        if (invoice != null) {
            invoices.add(invoice);
            invoice.setSubscription(this);
        }
    }
}
