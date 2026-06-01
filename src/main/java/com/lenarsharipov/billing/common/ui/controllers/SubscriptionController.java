package com.lenarsharipov.billing.common.ui.controllers;

import com.lenarsharipov.billing.common.ui.reqs.SubscriptionActivationRequest;
import com.lenarsharipov.billing.common.ui.reqs.SubscriptionDeactivationRequest;
import com.lenarsharipov.billing.subscription.services.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Void> activateSubscription(
            @PathVariable Long userId,
            @RequestBody @Valid SubscriptionActivationRequest request
    ) {
        subscriptionService.addSubscription(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/active")
    public ResponseEntity<Void> deactivateSubscription(
            @Valid SubscriptionDeactivationRequest request,
            @PathVariable Long userId
    ) {
        subscriptionService.deactivateSubscription(userId, request);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/active")
    public ResponseEntity<String> getActiveSubscription(
            @PathVariable Long userId
    ) {
        var resp = subscriptionService.getActiveSubscription(userId);
        return ResponseEntity.ok(resp);
    }

}
