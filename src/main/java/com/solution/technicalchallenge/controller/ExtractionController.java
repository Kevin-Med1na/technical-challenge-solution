package com.solution.technicalchallenge.controller;

import com.solution.technicalchallenge.dto.extraction.ExtractionRequestDto;
import com.solution.technicalchallenge.dto.extraction.ExtractionResponseDto;
import com.solution.technicalchallenge.dto.product.ProductResponseDto;
import com.solution.technicalchallenge.service.ExtractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/extractions")
@RequiredArgsConstructor
public class ExtractionController {

    private final ExtractionService extractionService;

    @PostMapping
    public ResponseEntity<ExtractionResponseDto> create(@Valid @RequestBody ExtractionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(extractionService.createJob(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtractionResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(extractionService.findById(id));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductResponseDto>> getProducts(@PathVariable UUID id) {
        return ResponseEntity.ok(extractionService.getProductsByJob(id));
    }
}
