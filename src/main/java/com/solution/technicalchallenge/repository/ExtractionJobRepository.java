package com.solution.technicalchallenge.repository;

import com.solution.technicalchallenge.entity.ExtractionJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExtractionJobRepository extends JpaRepository<ExtractionJob, UUID> {

}
