package com.lenarsharipov.billing.subscription.services;

import com.lenarsharipov.billing.common.ui.reqs.SubscriptionActivationRequest;
import com.lenarsharipov.billing.common.ui.reqs.SubscriptionDeactivationRequest;
import com.lenarsharipov.billing.subscription.usecases.ActivateSubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final ActivateSubscriptionUseCase activateUseCase;

    public void addSubscription(
            Long userId,
            SubscriptionActivationRequest request
    ) {
        activateUseCase.execute(userId, request);
    }

    public void deactivateSubscription(
            Long userId,
            SubscriptionDeactivationRequest request
    ) {

    }

    public String getActiveSubscription(Long userId) {
        return "";
    }
}

