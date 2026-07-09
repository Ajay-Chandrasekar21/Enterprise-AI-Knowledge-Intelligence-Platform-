package com.enterprise.eakip.core.common.saas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(subscriptionRepository);
    }

    @Test
    void calculateCost_ValidSubscription_ReturnsCalculatedInvoice() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        Subscription subscription = Subscription.builder()
                .tenantId(tenantId)
                .planTier("ENTERPRISE")
                .consumedTokens(2000000L) // 2M tokens
                .maxTokenQuota(10000000L)
                .costPerMillionTokens(10.00) // $10 per million
                .build();

        when(subscriptionRepository.findByTenantId(tenantId)).thenReturn(Optional.of(subscription));

        // Act
        Map<String, Object> invoice = billingService.calculateCost(tenantId);

        // Assert
        assertNotNull(invoice);
        assertEquals("ENTERPRISE", invoice.get("planTier"));
        assertEquals(2000000L, invoice.get("consumedTokens"));
        assertEquals(20.0, invoice.get("estimatedCost")); // 2M * 10 / 1M = $20.0
    }
}
