package com.enterprise.eakip.api.controller;

import com.enterprise.eakip.api.dto.ReservationResponse;
import com.enterprise.eakip.api.mapper.DomainMapper;
import com.enterprise.eakip.core.common.dto.ApiResponse;
import com.enterprise.eakip.core.domain.model.Reservation;
import com.enterprise.eakip.core.usecase.service.ReservationService;
import com.enterprise.eakip.security.service.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Hold API", description = "Endpoints for reserving unavailable books and cancel holds")
public class ReservationController {

    private final ReservationService reservationService;
    private final DomainMapper domainMapper;

    @PostMapping("/books/{bookId}/reserve")
    @Operation(summary = "Place a reservation hold", description = "Queues user in waiting list for unavailable copies")
    public ResponseEntity<ApiResponse<ReservationResponse>> reserveBook(
            @PathVariable UUID bookId,
            @AuthenticationPrincipal UserPrincipal principal) {
            
        Reservation reservation = reservationService.reserveBook(principal.getId(), bookId);
        return ResponseEntity.ok(ApiResponse.success("Book reserved successfully", domainMapper.toReservationResponse(reservation)));
    }

    @PostMapping("/{reservationId}/cancel")
    @Operation(summary = "Cancel active reservation queue hold", description = "Removes hold and re-calculates subsequent queue positions")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable UUID reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok(ApiResponse.emptySuccess("Reservation cancelled successfully"));
    }
}
