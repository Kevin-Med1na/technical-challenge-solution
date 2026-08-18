package com.solution.technicalchallenge.controller;

import com.solution.technicalchallenge.dto.product.ProductRequestDto;
import com.solution.technicalchallenge.dto.product.ProductResponseDto;
import com.solution.technicalchallenge.dto.product.ProductUpdateDto;
import com.solution.technicalchallenge.exception.ResourceNotFoundException;
import com.solution.technicalchallenge.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final UUID PRODUCT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createReturnsCreatedProduct() throws Exception {
        ProductResponseDto response = ProductResponseDto.builder()
                .id(PRODUCT_ID)
                .externalId(1)
                .name("Blue Top")
                .price("Rs. 500")
                .category("Women > Tops")
                .availability("In Stock")
                .condition("New")
                .brand("Polo")
                .sourceUrl("https://automationexercise.com/product_details/1")
                .build();

        when(productService.create(any(ProductRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Blue Top",
                                  "externalId": 1,
                                  "price": "Rs. 500",
                                  "category": "Women > Tops",
                                  "availability": "In Stock",
                                  "condition": "New",
                                  "brand": "Polo",
                                  "sourceUrl": "https://automationexercise.com/product_details/1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Blue Top"))
                .andExpect(jsonPath("$.externalId").value(1))
                .andExpect(jsonPath("$.brand").value("Polo"));

        verify(productService).create(any(ProductRequestDto.class));
    }

    @Test
    void createReturnsBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "externalId": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));

        verifyNoInteractions(productService);
    }

    @Test
    void findAllReturnsProducts() throws Exception {
        ProductResponseDto firstProduct = ProductResponseDto.builder()
                .id(PRODUCT_ID)
                .externalId(1)
                .name("Blue Top")
                .build();
        ProductResponseDto secondProduct = ProductResponseDto.builder()
                .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .externalId(2)
                .name("Men Tshirt")
                .build();

        when(productService.findAll()).thenReturn(List.of(firstProduct, secondProduct));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$[0].name").value("Blue Top"))
                .andExpect(jsonPath("$[1].name").value("Men Tshirt"));
    }

    @Test
    void findByIdReturnsProduct() throws Exception {
        ProductResponseDto response = ProductResponseDto.builder()
                .id(PRODUCT_ID)
                .externalId(1)
                .name("Blue Top")
                .build();

        when(productService.findById(PRODUCT_ID)).thenReturn(response);

        mockMvc.perform(get("/products/{id}", PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Blue Top"));
    }

    @Test
    void findByIdReturnsNotFoundWhenProductDoesNotExist() throws Exception {
        when(productService.findById(PRODUCT_ID))
                .thenThrow(new ResourceNotFoundException("Product not found with id: " + PRODUCT_ID));

        mockMvc.perform(get("/products/{id}", PRODUCT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Product not found with id: " + PRODUCT_ID));
    }

    @Test
    void updateReturnsUpdatedProduct() throws Exception {
        ProductResponseDto response = ProductResponseDto.builder()
                .id(PRODUCT_ID)
                .externalId(1)
                .name("Updated Blue Top")
                .price("Rs. 650")
                .build();

        when(productService.update(any(UUID.class), any(ProductUpdateDto.class))).thenReturn(response);

        mockMvc.perform(patch("/products/{id}", PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Blue Top",
                                  "price": "Rs. 650"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Updated Blue Top"))
                .andExpect(jsonPath("$.price").value("Rs. 650"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/products/{id}", PRODUCT_ID))
                .andExpect(status().isNoContent());

        verify(productService).delete(PRODUCT_ID);
    }
}
