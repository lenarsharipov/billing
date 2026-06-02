package com.lenarsharipov.billing.subscription.usecases;

import com.lenarsharipov.billing.common.exceptions.SubscriptionBusinessRuleException;
import com.lenarsharipov.billing.common.ui.reqs.SubscriptionDeactivationRequest;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import com.lenarsharipov.billing.subscription.services.OutboxProcessor;
import com.lenarsharipov.billing.subscription.services.SubscriptionDomainService;
import com.lenarsharipov.billing.subscription.validators.SubscriptionBusinessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivateSubscriptionUseCaseTest {

    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private SubscriptionDomainService domainService;
    @Mock private OutboxProcessor outboxProcessor;

    @Spy private SubscriptionBusinessValidator validator;

    @InjectMocks
    private DeactivateSubscriptionUseCase deactivateUseCase;

    private final Long userId = 1L;
    private SubscriptionDeactivationRequest request;
    private Subscription mockSubscription;

    @BeforeEach
    void setUp() {
        request = new SubscriptionDeactivationRequest(2L);
        mockSubscription = Subscription.builder()
                .id(55L)
                .userId(userId)
                .state(Subscription.State.ACTIVATED)
                .build();
    }

    @Test
    @DisplayName("Успешная деактивация подписки пользователя")
    void shouldDeactivateSubscriptionSuccessfully() {
        when(subscriptionRepository.findByUserIdAndTariffIdAndState(userId, request.tariffId(), Subscription.State.ACTIVATED))
                .thenReturn(Optional.of(mockSubscription));

        deactivateUseCase.execute(userId, request);

        verify(subscriptionRepository).findByUserIdAndTariffIdAndState(userId, request.tariffId(), Subscription.State.ACTIVATED);
        verify(domainService).deactivateSubscription(mockSubscription);
        verify(outboxProcessor).publishImmediateDeactivation(mockSubscription.getId());
    }

    @Test
    @DisplayName("Ошибка деактивации: у пользователя нет активной подписки этого типа")
    void shouldThrowExceptionWhenActiveSubscriptionNotFound() {
        when(subscriptionRepository.findByUserIdAndTariffIdAndState(userId, request.tariffId(), Subscription.State.ACTIVATED))
                .thenReturn(Optional.empty());

        assertThrows(SubscriptionBusinessRuleException.class, () ->
                deactivateUseCase.execute(userId, request)
        );

        verify(domainService, never()).deactivateSubscription(any());
        verify(outboxProcessor, never()).publishImmediateDeactivation(any());
    }
}