package com.example.URL_Shortner.repository;

import com.example.URL_Shortner.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for persisting URL mappings. This is intentionally simple for now.
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
}
