package com.solution.technicalchallenge.repository;

import com.solution.technicalchallenge.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByExternalId(Integer externalId);
}
