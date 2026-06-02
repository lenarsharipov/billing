package com.lenarsharipov.billing.usercache.dto;

import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserProfileResponse(
        List<SubscriptionDto> activeSubscriptions,
        Page<InvoiceDto> invoicesPage
) {}
