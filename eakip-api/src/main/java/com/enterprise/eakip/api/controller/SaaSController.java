package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.core.common.saas.Tenant;
import com.enterprise.eakip.core.common.saas.TenantRepository;
import com.enterprise.eakip.core.common.saas.Subscription;
import com.enterprise.eakip.core.common.saas.SubscriptionRepository;
import com.enterprise.eakip.core.common.saas.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/saas")
@RequiredArgsConstructor
@Tag(name = "Enterprise SaaS AI API", description = "Endpoints for multi-tenancy controls, subscription quotas, and token billing calculators")
public class SaaSController {

    private final TenantRepository tenantRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BillingService billingService;

    // Fixed mock tenant ID for local admin checks
    private static final UUID MOCK_TENANT_UUID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @GetMapping("/tenants")
    @Operation(summary = "List all SaaS tenants", description = "Global Admin gateway listing isolated workspaces")
    public ResponseEntity<ApiResponse<List<Tenant>>> listTenants() {
        return ResponseEntity.ok(ApiResponse.success("Tenants loaded successfully", tenantRepository.findAll()));
    }

    @PostMapping("/tenants")
    @Operation(summary = "Register new isolated workspace tenant")
    public ResponseEntity<ApiResponse<Tenant>> createTenant(
            @RequestParam String name,
            @RequestParam(defaultValue = "#4F46E5") String primaryColor) {
            
        Tenant tenant = Tenant.builder()
                .name(name)
                .brandingPrimaryColor(primaryColor)
                .isActive(true)
                .build();
        
        tenant = tenantRepository.save(tenant);
        
        // Spawn default subscription
        Subscription sub = Subscription.builder()
                .tenantId(tenant.getId())
                .planTier("ENTERPRISE")
                .maxTokenQuota(10000000L) // 10M tokens
                .consumedTokens(125000L) // mock initial usage
                .costPerMillionTokens(12.00)
                .rateLimitPerMin(120)
                .build();
        subscriptionRepository.save(sub);

        return ResponseEntity.ok(ApiResponse.success("Tenant and subscription registered successfully", tenant));
    }

    @GetMapping("/billing")
    @Operation(summary = "Calculate current token billing costs", description = "Retrieves consumption cost calculations and monthly invoice estimators")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBillingStats(@RequestParam(required = false) UUID tenantId) {
        UUID target = tenantId != null ? tenantId : MOCK_TENANT_UUID;
        
        // Track mock consumption to simulate active dashboard charts
        billingService.trackUsage(target, 45000);
        
        Map<String, Object> invoice = billingService.calculateCost(target);
        return ResponseEntity.ok(ApiResponse.success("Billing statistics calculated", invoice));
    }

    @PostMapping("/tenants/branding")
    @Operation(summary = "Update tenant portal branding color schemes")
    public ResponseEntity<ApiResponse<Tenant>> updateBranding(
            @RequestParam UUID tenantId,
            @RequestParam String color,
            @RequestParam String logoUrl) {
            
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("No tenant matches: " + tenantId));
        
        tenant.setBrandingPrimaryColor(color);
        tenant.setBrandingLogoUrl(logoUrl);
        tenant = tenantRepository.save(tenant);
        return ResponseEntity.ok(ApiResponse.success("Tenant branding updated", tenant));
    }

    @GetMapping("/feature-flags")
    @Operation(summary = "Query SaaS feature flags status list")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getFeatureFlags() {
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("advancedRAGindexing", true);
        flags.put("mcpConnectorsDiscovery", true);
        flags.put("autonomousWorkflows", true);
        return ResponseEntity.ok(ApiResponse.success("Feature flags loaded", flags));
    }
}
