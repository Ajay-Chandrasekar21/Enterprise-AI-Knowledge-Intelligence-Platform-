from fastapi import APIRouter, Depends
from fastapi.responses import Response
from sqlalchemy.orm import Session
from eakip_core.domain.repositories import BookRepository, BorrowingRepository
from eakip_core.usecase.services import ExportService
from ..config.db import get_db
from ..config.security import require_role

router = APIRouter(prefix="/api/v1/reports", tags=["Reports Exporter API"])

def get_export_service(db: Session = Depends(get_db)) -> ExportService:
    return ExportService(
        book_repo=BookRepository(db),
        borrowing_repo=BorrowingRepository(db)
    )

@router.get("/books/csv", dependencies=[Depends(require_role(["ADMIN", "LIBRARIAN"]))])
def export_books(service: ExportService = Depends(get_export_service)):
    csv_data = service.export_books_to_csv()
    return Response(
        content=csv_data,
        media_type="text/csv",
        headers={"Content-Disposition": "attachment; filename=books_catalog_report.csv"}
    )

@router.get("/borrowings/csv", dependencies=[Depends(require_role(["ADMIN", "LIBRARIAN"]))])
def export_borrowings(service: ExportService = Depends(get_export_service)):
    csv_data = service.export_borrowings_to_csv()
    return Response(
        content=csv_data,
        media_type="text/csv",
        headers={"Content-Disposition": "attachment; filename=borrowings_report.csv"}
    )
