package com.solution.technicalchallenge.service;


import com.solution.technicalchallenge.dto.extraction.ExtractionRequestDto;
import com.solution.technicalchallenge.dto.extraction.ExtractionResponseDto;
import com.solution.technicalchallenge.dto.product.ProductResponseDto;
import com.solution.technicalchallenge.entity.ExtractionItem;
import com.solution.technicalchallenge.entity.ExtractionJob;
import com.solution.technicalchallenge.enums.ItemStatus;
import com.solution.technicalchallenge.enums.JobStatus;
import com.solution.technicalchallenge.exception.ResourceNotFoundException;
import com.solution.technicalchallenge.repository.ExtractionItemRepository;
import com.solution.technicalchallenge.repository.ExtractionJobRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ExtractionService {


    private final ExtractionJobRepository jobRepository;
    private final ExtractionItemRepository itemRepository;
    private final ExtractionProcessor extractionProcessor;
    private final ProductService productService;

    public ExtractionResponseDto createJob(ExtractionRequestDto dto) {
        ExtractionJob job = new ExtractionJob();
        job.setStatus(JobStatus.PENDING);
        job.setTotal(dto.getProductIds().size());
        job.setProcessed(0);
        job.setSuccessful(0);
        job.setFailed(0);

        ExtractionJob savedJob = jobRepository.save(job);

        for (Integer productId : dto.getProductIds()) {
            ExtractionItem item = new ExtractionItem();
            item.setJob(savedJob);
            item.setExternalProductId(productId);
            item.setStatus(ItemStatus.PENDING);
            itemRepository.save(item);
        }

        extractionProcessor.process(savedJob.getId());

        return toResponse(savedJob);
    }

    public ExtractionResponseDto findById(UUID id) {
        ExtractionJob job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extraction job not found with id: " + id));
        return toResponse(job);
    }

    public List<ProductResponseDto> getProductsByJob(UUID jobId) {
        ExtractionJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Extraction job not found with id: " + jobId));
        return itemRepository.findByJob(job).stream()
                .filter(item -> item.getProduct() != null)
                .map(item -> productService.toResponse(item.getProduct()))
                .toList();
    }

    private ExtractionResponseDto toResponse(ExtractionJob job) {
        return ExtractionResponseDto.builder()
                .id(job.getId())
                .status(job.getStatus())
                .total(job.getTotal())
                .processed(job.getProcessed())
                .successful(job.getSuccessful())
                .failed(job.getFailed())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
