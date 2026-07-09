import uuid
from typing import List, Optional
from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from eakip_core.common.dto import ApiResponse
from eakip_core.domain.models import Tenant, Subscription
from eakip_core.domain.repositories import TenantRepository, SubscriptionRepository
from eakip_core.usecase.services import BillingService
from ..config.db import get_db

router = APIRouter(prefix="/api/v1/saas", tags=["Enterprise SaaS AI API"])

MOCK_TENANT_UUID = uuid.UUID("99999999-9999-9999-9999-999999999999")

def get_billing_service(db: Session = Depends(get_db)) -> BillingService:
    return BillingService(SubscriptionRepository(db))

@router.get("/tenants", response_model=ApiResponse[List[dict]])
def list_tenants(db: Session = Depends(get_db)):
    tenants = TenantRepository(db).session.query(Tenant).all()
    # Map to dict list to preserve dynamic serialization properties
    result = [{
        "id": str(t.id),
        "name": t.name,
        "brandingPrimaryColor": t.branding_primary_color,
        "brandingLogoUrl": t.branding_logo_url,
        "isActive": t.is_active
    } for t in tenants]
    return ApiResponse.success_response(result, "Tenants loaded successfully")

@router.post("/tenants", response_model=ApiResponse[dict])
def create_tenant(
    name: str,
    primary_color: str = "#4F46E5",
    db: Session = Depends(get_db)
):
    tenant_repo = TenantRepository(db)
    sub_repo = SubscriptionRepository(db)

    tenant = Tenant(
        name=name,
        branding_primary_color=primary_color,
        is_active=True
    )
    tenant = tenant_repo.save(tenant)

    sub = Subscription(
        tenant_id=tenant.id,
        plan_tier="ENTERPRISE",
        max_token_quota=10000000,
        consumed_tokens=125000,
        cost_per_million_tokens=12.00,
        rate_limit_per_min=120
    )
    sub_repo.save(sub)

    result = {
        "id": str(tenant.id),
        "name": tenant.name,
        "brandingPrimaryColor": tenant.branding_primary_color,
        "brandingLogoUrl": tenant.branding_logo_url,
        "isActive": tenant.is_active
    }
    return ApiResponse.success_response(result, "Tenant and subscription registered successfully")

@router.get("/billing", response_model=ApiResponse[dict])
def get_billing_stats(
    tenant_id: Optional[uuid.UUID] = None,
    service: BillingService = Depends(get_billing_service)
):
    target = tenant_id if tenant_id else MOCK_TENANT_UUID
    
    # Track mock consumption to match active metrics triggers
    service.track_usage(target, 45000)
    
    invoice = service.calculate_cost(target)
    # Convert tenant ID to string for safety
    invoice["tenantId"] = str(invoice["tenantId"])
    return ApiResponse.success_response(invoice, "Billing statistics calculated")

@router.post("/tenants/branding", response_model=ApiResponse[dict])
def update_branding(
    tenant_id: uuid.UUID,
    color: str,
    logo_url: str,
    db: Session = Depends(get_db)
):
    tenant_repo = TenantRepository(db)
    tenant = tenant_repo.find_by_id(tenant_id)
    if not tenant:
        raise ValueError(f"No tenant matches: {tenant_id}")

    tenant.branding_primary_color = color
    tenant.branding_logo_url = logo_url
    tenant = tenant_repo.save(tenant)

    result = {
        "id": str(tenant.id),
        "name": tenant.name,
        "brandingPrimaryColor": tenant.branding_primary_color,
        "brandingLogoUrl": tenant.branding_logo_url,
        "isActive": tenant.is_active
    }
    return ApiResponse.success_response(result, "Tenant branding updated")

@router.get("/feature-flags", response_model=ApiResponse[dict])
def get_feature_flags():
    flags = {
        "advancedRAGindexing": True,
        "mcpConnectorsDiscovery": True,
        "autonomousWorkflows": True
    }
    return ApiResponse.success_response(flags, "Feature flags loaded")
