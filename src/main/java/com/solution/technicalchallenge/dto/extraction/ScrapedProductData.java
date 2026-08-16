package com.solution.technicalchallenge.dto.extraction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapedProductData {

    private String name;
    private String price;
    private String category;
    private String availability;
    private String condition;
    private String brand;
}
