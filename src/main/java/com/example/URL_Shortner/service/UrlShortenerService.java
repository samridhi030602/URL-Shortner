package com.example.URL_Shortner.service;

import com.example.URL_Shortner.dto.ShortenUrlRequest;
import com.example.URL_Shortner.dto.ShortenUrlResponse;
import com.example.URL_Shortner.entity.UrlMapping;
import com.example.URL_Shortner.repository.UrlMappingRepository;
import java.net.URI;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Simple service layer for the initial version of the shortener flow.
@Service
public class UrlShortenerService {

    private static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final UrlMappingRepository urlMappingRepository;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    // Create a mapping and return the short code. This uses the persisted database ID
    // so the encoding is deterministic and avoids collisions once the entity is saved.
    @Transactional
    public ShortenUrlResponse createShortUrl(ShortenUrlRequest request) {
        String originalUrl = validateOriginalUrl(request.getOriginalUrl());

        Optional<UrlMapping> existingMapping = urlMappingRepository.findByOriginalUrl(originalUrl);
        if (existingMapping.isPresent()) {
            return new ShortenUrlResponse(existingMapping.get().getShortCode());
        }

        String shortCode = resolveShortCode(request.getCustomAlias());

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping.setShortCode(shortCode != null ? shortCode : "");

        UrlMapping savedMapping = urlMappingRepository.save(mapping);

        String finalShortCode = shortCode != null
                ? shortCode
                : encodeBase62(savedMapping.getId());
        savedMapping.setShortCode(finalShortCode);
        urlMappingRepository.save(savedMapping);

        return new ShortenUrlResponse(finalShortCode);
    }

    // Resolve the original URL from the database using the short code.
    @Transactional(readOnly = true)
    public Optional<String> resolveOriginalUrl(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode)
                .map(UrlMapping::getOriginalUrl);
    }

    private String resolveShortCode(String customAlias) {
        if (customAlias == null || customAlias.isBlank()) {
            return null;
        }

        String normalizedAlias = customAlias.trim();
        if (urlMappingRepository.existsByShortCode(normalizedAlias)) {
            throw new IllegalArgumentException("Duplicate alias: " + normalizedAlias);
        }

        return normalizedAlias;
    }

    private String validateOriginalUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new IllegalArgumentException("originalUrl is required");
        }

        try {
            URI.create(originalUrl).toURL();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid URL: " + originalUrl, ex);
        }

        return originalUrl;
    }

    private String encodeBase62(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Database ID must be positive");
        }

        StringBuilder builder = new StringBuilder();
        long numericValue = value;

        while (numericValue > 0) {
            int remainder = (int) (numericValue % 62);
            builder.append(BASE62_ALPHABET.charAt(remainder));
            numericValue /= 62;
        }

        return builder.reverse().toString();
    }
}
