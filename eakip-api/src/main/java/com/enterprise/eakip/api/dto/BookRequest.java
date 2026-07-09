package com.enterprise.eakip.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    private String description;

    private UUID publisherId;

    private UUID categoryId;

    private List<UUID> authorIds;

    private String edition;

    private String language;

    private String shelf;

    private String rack;

    @NotNull(message = "Total copies count is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
}
