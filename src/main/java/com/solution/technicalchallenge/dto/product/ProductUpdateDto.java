package com.solution.technicalchallenge.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateDto {

    private String name;
    private String price;
    private String category;
    private String availability;
    private String condition;
    private String brand;
    private String sourceUrl;
}
