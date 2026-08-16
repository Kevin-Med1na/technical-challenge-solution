package com.solution.technicalchallenge.dto.product;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private UUID id;
    private Integer externalId;
    private String name;
    private String price;
    private String category;
    private String availability;
    private String condition;
    private String brand;
    private String sourceUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
