package com.lenarsharipov.billing.subscription.usecases;

import com.lenarsharipov.billing.common.exceptions.SubscriptionBusinessRuleException;
import com.lenarsharipov.billing.common.ui.reqs.SubscriptionActivationRequest;
import com.lenarsharipov.billing.subscription.entities.Invoice;
import com.lenarsharipov.billing.subscription.entities.Subscription;
import com.lenarsharipov.billing.subscription.entities.Tariff;
import com.lenarsharipov.billing.subscription.repositories.SubscriptionRepository;
import com.lenarsharipov.billing.subscription.repositories.TariffRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateSubscriptionUseCaseTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private TariffRepository tariffRepository;
    @Mock
    private SubscriptionDomainService domainService;
    @Mock
    private OutboxProcessor outboxProcessor;

    @Spy
    private SubscriptionBusinessValidator validator;

    @InjectMocks
    private ActivateSubscriptionUseCase activateUseCase;

    private final Long userId = 1L;
    private SubscriptionActivationRequest request;
    private Tariff mockTariff;

    @BeforeEach
    void setUp() {
        request = new SubscriptionActivationRequest(2L, LocalDate.now());
        mockTariff = Tariff.builder()
                .id(2L)
                .name("PRO")
                .amount(BigDecimal.valueOf(200))
                .build();
    }

    @Test
    @DisplayName("Успешная активация подписки и выставление первичного счета")
    void shouldActivateSubscriptionSuccessfully() {
        when(subscriptionRepository.existsByUserIdAndState(userId, Subscription.State.ACTIVATED))
                .thenReturn(false);
        when(tariffRepository.findById(request.tariffId()))
                .thenReturn(Optional.of(mockTariff));

        Invoice mockInvoice = Invoice.builder()
                .id(100L)
                .userId(userId)
                .build();
        when(domainService.createNewSubscription(eq(userId), eq(mockTariff), any(LocalDate.class)))
                .thenReturn(mockInvoice);

        activateUseCase.execute(userId, request);

        verify(subscriptionRepository).existsByUserIdAndState(userId, Subscription.State.ACTIVATED);
        verify(domainService).createNewSubscription(userId, mockTariff, request.activationDate());
        verify(outboxProcessor).publishImmediate(mockInvoice);
    }

    @Test
    @DisplayName("Ошибка активации: у пользователя уже есть активная подписка")
    void shouldThrowExceptionWhenUserHasActiveSubscription() {
        when(subscriptionRepository.existsByUserIdAndState(userId, Subscription.State.ACTIVATED))
                .thenReturn(true);

        assertThrows(SubscriptionBusinessRuleException.class, () ->
                activateUseCase.execute(userId, request)
        );

        verify(domainService, never()).createNewSubscription(any(), any(), any());
    }
}