package com.enterprise.eakip.api.dto;

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
public class BookResponse {
    private UUID id;
    private String title;
    private String isbn;
    private String description;
    private String publisherName;
    private String categoryName;
    private List<String> authorNames;
    private String edition;
    private String language;
    private String shelf;
    private String rack;
    private Integer totalCopies;
    private Integer availableCopies;
}
