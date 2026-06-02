package com.lenarsharipov.billing.common.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class DateTimeUtil {

    public Instant toStartOfDayUtc(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    /**
     * Извлекает день месяца (1-31) из Instant по календарю UTC
     */
    public int getDayOfMonthUtc(Instant instant) {
        if (instant == null) {
            return 0;
        }
        return instant.atZone(ZoneOffset.UTC).getDayOfMonth();
    }

    /**
     * Проверяет, совпадают ли календарный день, месяц и год у двух Instant в UTC
     */
    public boolean isSameDayUtc(Instant first, Instant second) {
        if (first == null || second == null) {
            return false;
        }
        ZonedDateTime dt1 = first.atZone(ZoneOffset.UTC);
        ZonedDateTime dt2 = second.atZone(ZoneOffset.UTC);

        return dt1.getYear() == dt2.getYear() &&
                dt1.getMonth() == dt2.getMonth() &&
                dt1.getDayOfMonth() == dt2.getDayOfMonth();
    }
}
