package com.enterprise.eakip.core.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String errorCode;
    private String message;
    private String correlationId;
    private List<String> details;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static ApiErrorResponse of(String errorCode, String message, String correlationId, List<String> details) {
        return ApiErrorResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .correlationId(correlationId)
                .details(details)
                .build();
    }
}
