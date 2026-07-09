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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saas_tenants")
public class Tenant extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "branding_primary_color", length = 50)
    @Builder.Default
    private String brandingPrimaryColor = "#4F46E5"; // Default primary HSL/Hex color

    @Column(name = "branding_logo_url", length = 255)
    private String brandingLogoUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
