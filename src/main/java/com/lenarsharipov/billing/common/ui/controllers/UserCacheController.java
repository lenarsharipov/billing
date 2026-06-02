package com.lenarsharipov.billing.common.ui.controllers;

import com.lenarsharipov.billing.subscription.dtos.InvoiceDto;
import com.lenarsharipov.billing.subscription.dtos.SubscriptionDto;
import com.lenarsharipov.billing.usercache.UserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cache/users/{userId}")
@RequiredArgsConstructor
public class UserCacheController {

    private final UserCacheService userCacheService;

    @GetMapping("/subscriptions/active")
    public ResponseEntity<List<SubscriptionDto>> getActiveSubscriptions(
            @PathVariable Long userId
    ) {
        List<SubscriptionDto> response = userCacheService.getActiveSubscriptions(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoices")
    public ResponseEntity<Page<InvoiceDto>> getUserInvoices(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        Page<InvoiceDto> response = userCacheService.getUserInvoices(userId, pageable);
        return ResponseEntity.ok(response);
    }
}