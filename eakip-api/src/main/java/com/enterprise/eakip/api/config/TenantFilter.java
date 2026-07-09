package com.enterprise.eakip.api.config;

import com.enterprise.eakip.core.common.saas.TenantContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String tenantId = httpRequest.getHeader(TENANT_HEADER);
            if (tenantId != null && !tenantId.trim().isEmpty()) {
                TenantContext.setCurrentTenant(tenantId);
            } else {
                TenantContext.setCurrentTenant("default-tenant"); // Fallback
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
