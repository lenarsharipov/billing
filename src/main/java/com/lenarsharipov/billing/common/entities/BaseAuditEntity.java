package com.lenarsharipov.billing.common.entities;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.Instant;

@Data
@MappedSuperclass
public abstract class BaseAuditEntity {

    private Instant createdAt;

    private Instant lastModifiedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = Instant.now();
    }
}
