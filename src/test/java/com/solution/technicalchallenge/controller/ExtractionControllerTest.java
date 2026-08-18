package com.solution.technicalchallenge.controller;

import com.solution.technicalchallenge.dto.extraction.ExtractionRequestDto;
import com.solution.technicalchallenge.dto.extraction.ExtractionResponseDto;
import com.solution.technicalchallenge.dto.product.ProductResponseDto;
import com.solution.technicalchallenge.enums.JobStatus;
import com.solution.technicalchallenge.exception.ResourceNotFoundException;
import com.solution.technicalchallenge.service.ExtractionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExtractionController.class)
class ExtractionControllerTest {

    private static final UUID JOB_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExtractionService extractionService;

    @Test
    void createReturnsAcceptedJob() throws Exception {
        ExtractionResponseDto response = ExtractionResponseDto.builder()
                .id(JOB_ID)
                .status(JobStatus.PENDING)
                .total(2)
                .processed(0)
                .successful(0)
                .failed(0)
                .build();

        when(extractionService.createJob(any(ExtractionRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/extractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productIds": [1, 2]
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(JOB_ID.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.processed").value(0));
    }

    @Test
    void createReturnsBadRequestWhenProductIdsAreEmpty() throws Exception {
        mockMvc.perform(post("/extractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productIds": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productIds").value("Product IDs list cannot be empty"));

        verifyNoInteractions(extractionService);
    }

    @Test
    void findByIdReturnsJob() throws Exception {
        ExtractionResponseDto response = ExtractionResponseDto.builder()
                .id(JOB_ID)
                .status(JobStatus.COMPLETED)
                .total(2)
                .processed(2)
                .successful(2)
                .failed(0)
                .build();

        when(extractionService.findById(JOB_ID)).thenReturn(response);

        mockMvc.perform(get("/extractions/{id}", JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(JOB_ID.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.successful").value(2));
    }

    @Test
    void findByIdReturnsNotFoundWhenJobDoesNotExist() throws Exception {
        when(extractionService.findById(JOB_ID))
                .thenThrow(new ResourceNotFoundException("Extraction job not found with id: " + JOB_ID));

        mockMvc.perform(get("/extractions/{id}", JOB_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Extraction job not found with id: " + JOB_ID));
    }

    @Test
    void getProductsReturnsProductsForJob() throws Exception {
        ProductResponseDto product = ProductResponseDto.builder()
                .id(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .externalId(1)
                .name("Blue Top")
                .build();

        when(extractionService.getProductsByJob(JOB_ID)).thenReturn(List.of(product));

        mockMvc.perform(get("/extractions/{id}/products", JOB_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].externalId").value(1))
                .andExpect(jsonPath("$[0].name").value("Blue Top"));
    }
}
