package com.solution.technicalchallenge.service;

import com.solution.technicalchallenge.dto.extraction.ScrapedProductData;
import com.solution.technicalchallenge.entity.ExtractionItem;
import com.solution.technicalchallenge.entity.ExtractionJob;
import com.solution.technicalchallenge.entity.Product;
import com.solution.technicalchallenge.enums.ItemStatus;
import com.solution.technicalchallenge.enums.JobStatus;
import com.solution.technicalchallenge.repository.ExtractionItemRepository;
import com.solution.technicalchallenge.repository.ExtractionJobRepository;
import com.solution.technicalchallenge.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExtractionProcessor {

    private final ExtractionJobRepository jobRepository;
    private final ExtractionItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final ScrapingService scrapingService;

    private static final String SOURCE_URL = "https://automationexercise.com/product_details/";

    @Async("extractionExecutor")
    public void process(UUID jobId) {
        ExtractionJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        try {
            job.setStatus(JobStatus.PROCESSING);
            jobRepository.save(job);

            List<ExtractionItem> items = itemRepository.findByJob(job);

            for (ExtractionItem item : items) {
                try {
                    ScrapedProductData data = scrapingService.scrape(item.getExternalProductId());

                    Product product = productRepository
                            .findByExternalId(item.getExternalProductId())
                            .orElse(new Product()); //pequeña optimizacion para no crear un nuevo producto si ya existe uno con el mismo externalId

                    product.setExternalId(item.getExternalProductId());
                    product.setName(data.getName());
                    product.setPrice(data.getPrice());
                    product.setCategory(data.getCategory());
                    product.setAvailability(data.getAvailability());
                    product.setCondition(data.getCondition());
                    product.setBrand(data.getBrand());
                    product.setSourceUrl(SOURCE_URL + item.getExternalProductId());

                    Product savedProduct = productRepository.save(product);

                    item.setProduct(savedProduct);
                    item.setStatus(ItemStatus.SUCCESS);
                    job.setSuccessful(job.getSuccessful() + 1);

                } catch (Exception e) {
                    item.setStatus(ItemStatus.FAILED);
                    item.setErrorMessage(e.getMessage());
                    job.setFailed(job.getFailed() + 1);
                }

                job.setProcessed(job.getProcessed() + 1);
                itemRepository.save(item);
                jobRepository.save(job);
            }

            if (job.getFailed() == 0) {
                job.setStatus(JobStatus.COMPLETED);
            } else if (job.getSuccessful() == 0) {
                job.setStatus(JobStatus.FAILED);
            } else {
                job.setStatus(JobStatus.COMPLETED_WITH_ERRORS);
            }

        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
        }

        jobRepository.save(job);
    }
}
