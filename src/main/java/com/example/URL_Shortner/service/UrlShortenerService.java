package com.example.URL_Shortner.service;

import com.example.URL_Shortner.dto.ShortenUrlRequest;
import com.example.URL_Shortner.dto.ShortenUrlResponse;
import com.example.URL_Shortner.entity.UrlMapping;
import com.example.URL_Shortner.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

// Simple service layer for the initial version of the shortener flow.
@Service
public class UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    // Initial implementation: generate a temporary short code and save the mapping.
    @Transactional
    public ShortenUrlResponse createShortUrl(ShortenUrlRequest request) {
        if (request.getOriginalUrl() == null || request.getOriginalUrl().isBlank()) {
            throw new IllegalArgumentException("originalUrl is required");
        }

        String shortCode = request.getCustomAlias() != null && !request.getCustomAlias().isBlank()
                ? request.getCustomAlias()
                : generateTemporaryShortCode();

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(request.getOriginalUrl());
        mapping.setShortCode(shortCode);

        urlMappingRepository.save(mapping);

        return new ShortenUrlResponse(shortCode);
    }

    private String generateTemporaryShortCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
