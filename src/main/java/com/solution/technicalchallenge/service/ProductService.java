package com.solution.technicalchallenge.service;

import com.solution.technicalchallenge.dto.product.ProductRequestDto;
import com.solution.technicalchallenge.dto.product.ProductResponseDto;
import com.solution.technicalchallenge.dto.product.ProductUpdateDto;
import com.solution.technicalchallenge.entity.Product;
import com.solution.technicalchallenge.exception.ResourceNotFoundException;
import com.solution.technicalchallenge.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto create(ProductRequestDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setExternalId(dto.getExternalId());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setAvailability(dto.getAvailability());
        product.setCondition(dto.getCondition());
        product.setBrand(dto.getBrand());
        product.setSourceUrl(dto.getSourceUrl());
        return toResponse(productRepository.save(product));
    }

    public List<ProductResponseDto> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponseDto findById(UUID id) {
        return toResponse(findProductOrThrow(id));
    }

    public ProductResponseDto update(UUID id, ProductUpdateDto dto) {
        Product product = findProductOrThrow(id);

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getAvailability() != null) product.setAvailability(dto.getAvailability());
        if (dto.getCondition() != null) product.setCondition(dto.getCondition());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getSourceUrl() != null) product.setSourceUrl(dto.getSourceUrl());

        return toResponse(productRepository.save(product));
    }

    public void delete(UUID id) {
        productRepository.delete(findProductOrThrow(id));
    }
    //consultar en metodo privado para evitar repeticiones de codigo y facilitar el cambio de mensaje de error a futuro
    private Product findProductOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    //metodo de mapeo que usan todos los metodos publicos
    public ProductResponseDto toResponse(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .externalId(product.getExternalId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .availability(product.getAvailability())
                .condition(product.getCondition())
                .brand(product.getBrand())
                .sourceUrl(product.getSourceUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
