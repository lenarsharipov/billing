package com.lenarsharipov.billing.common.mappers;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class DateTimeMapper {

    public Instant toStartOfDayUtc(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
