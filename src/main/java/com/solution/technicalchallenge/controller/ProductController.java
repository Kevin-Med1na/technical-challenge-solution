package com.solution.technicalchallenge.controller;

import com.solution.technicalchallenge.dto.product.ProductRequestDto;
import com.solution.technicalchallenge.dto.product.ProductResponseDto;
import com.solution.technicalchallenge.dto.product.ProductUpdateDto;
import com.solution.technicalchallenge.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    @GetMapping

    public ResponseEntity<List<ProductResponseDto>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable UUID id,
            @RequestBody ProductUpdateDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
