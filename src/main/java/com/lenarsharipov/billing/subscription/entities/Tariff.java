package com.lenarsharipov.billing.subscription.entities;

import com.lenarsharipov.billing.common.entities.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tariffs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal amount;
}
