package com.lenarsharipov.billing.common.ui.reqs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import static com.lenarsharipov.billing.common.constants.CommonConstants.ACTIVATION_DATE_PATTERN;

public record SubscriptionActivationRequest(

        @NotNull(message = "Не может быть пустым или null")
        Long tariffId,

        @NotNull(message = "Дата активации обязательна")
        @FutureOrPresent(message = "Дата активация не может быть в прошлом")
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = ACTIVATION_DATE_PATTERN
        )
        LocalDate activationDate
) {
}
