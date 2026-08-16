package com.solution.technicalchallenge.dto.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    private Integer externalId;
    private String price;
    private String category;
    private String availability;
    private String condition;
    private String brand;
    private String sourceUrl;
}
