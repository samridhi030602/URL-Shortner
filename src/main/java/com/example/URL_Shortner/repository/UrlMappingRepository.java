package com.example.URL_Shortner.repository;

import com.example.URL_Shortner.entity.UrlMapping;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for persisting URL mappings. This is intentionally simple for now.
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);
}
