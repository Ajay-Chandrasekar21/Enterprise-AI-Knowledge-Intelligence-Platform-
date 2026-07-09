package com.enterprise.eakip.core.common.saas;

import com.enterprise.eakip.core.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saas_subscriptions")
public class Subscription extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;

    @Column(name = "plan_tier", nullable = false, length = 50)
    @Builder.Default
    private String planTier = "FREE"; // FREE, TEAM, ENTERPRISE

    @Column(name = "max_token_quota", nullable = false)
    @Builder.Default
    private Long maxTokenQuota = 1000000L; // 1M tokens limit

    @Column(name = "consumed_tokens", nullable = false)
    @Builder.Default
    private Long consumedTokens = 0L;

    @Column(name = "rate_limit_per_min", nullable = false)
    @Builder.Default
    private Integer rateLimitPerMin = 60;

    @Column(name = "cost_per_million_tokens", nullable = false)
    @Builder.Default
    private Double costPerMillionTokens = 15.00; // $15 per million input/output tokens
}
