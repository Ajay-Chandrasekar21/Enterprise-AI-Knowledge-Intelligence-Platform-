package com.enterprise.eakip.core.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private String query = "";

    @Min(value = 0, message = "Page number cannot be negative")
    private int pageNumber = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int pageSize = 10;

    private String sortBy = "id";

    private String sortDirection = "ASC";
}
