from contextvars import ContextVar

_current_tenant: ContextVar[str] = ContextVar("current_tenant", default="default-tenant")

class TenantContext:
    @staticmethod
    def get_current_tenant() -> str:
        return _current_tenant.get()

    @staticmethod
    def set_current_tenant(tenant_id: str) -> None:
        _current_tenant.set(tenant_id)

    @staticmethod
    def clear() -> None:
        _current_tenant.set("default-tenant")
