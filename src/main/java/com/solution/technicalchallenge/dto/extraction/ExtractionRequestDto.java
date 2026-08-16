package com.solution.technicalchallenge.dto.extraction;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

@NoArgsConstructor
public class ExtractionRequestDto {

    @NotEmpty(message = "Product IDs list cannot be empty")
    private List<Integer> productIds;
}
