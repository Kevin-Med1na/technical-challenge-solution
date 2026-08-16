package com.solution.technicalchallenge.repository;

import com.solution.technicalchallenge.entity.ExtractionItem;
import com.solution.technicalchallenge.entity.ExtractionJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExtractionItemRepository extends JpaRepository<ExtractionItem, UUID> {
    List<ExtractionItem> findByJob(ExtractionJob job);
}
