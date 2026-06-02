package com.lenarsharipov.billing.common.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OutboxEvent extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;

    private String aggregateId;

    private String payload;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Builder.Default
    private int attempts = 0;

    private Instant nextAttemptAt;

    public enum Status {
        PENDING,  // Ожидает отправки
        SENT,     // Успешно отправлено
        DLQ       // Превышено N попыток, ожидает ручного разбора
    }

    public enum AggregateType {
        INVOICE,
        SUBSCRIPTION_DEACTIVATED
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        if (this.nextAttemptAt == null) {
            this.nextAttemptAt = Instant.now();
        }
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }
}

