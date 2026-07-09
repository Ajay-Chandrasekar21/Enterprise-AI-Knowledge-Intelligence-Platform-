package com.enterprise.eakip.core.common.saas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void trackUsage(UUID tenantId, long tokensCount) {
        log.info("Tracking SaaS token usage. Tenant: {}, Tokens: {}", tenantId, tokensCount);
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseGet(() -> createDefaultSubscription(tenantId));

        subscription.setConsumedTokens(subscription.getConsumedTokens() + tokensCount);
        subscriptionRepository.save(subscription);
    }

    public Map<String, Object> calculateCost(UUID tenantId) {
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseGet(() -> createDefaultSubscription(tenantId));

        long tokens = subscription.getConsumedTokens();
        double cost = (tokens / 1000000.0) * subscription.getCostPerMillionTokens();

        Map<String, Object> invoice = new HashMap<>();
        invoice.put("tenantId", tenantId);
        invoice.put("planTier", subscription.getPlanTier());
        invoice.put("consumedTokens", tokens);
        invoice.put("quotaLimit", subscription.getMaxTokenQuota());
        invoice.put("estimatedCost", cost);
        invoice.put("currency", "USD");
        return invoice;
    }

    private Subscription createDefaultSubscription(UUID tenantId) {
        Subscription sub = Subscription.builder()
                .tenantId(tenantId)
                .planTier("FREE")
                .maxTokenQuota(1000000L)
                .consumedTokens(0L)
                .costPerMillionTokens(15.00)
                .rateLimitPerMin(60)
                .build();
        return subscriptionRepository.save(sub);
    }
}
