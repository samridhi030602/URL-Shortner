package com.example.URL_Shortner.service;

import com.example.URL_Shortner.dto.ShortenUrlRequest;
import com.example.URL_Shortner.dto.ShortenUrlResponse;
import com.example.URL_Shortner.entity.UrlMapping;
import com.example.URL_Shortner.repository.UrlMappingRepository;
import java.net.URI;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
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

    // Create a mapping and return the short code. Base62 is chosen because it creates compact,
    // URL-safe codes without relying on random strings or collisions after persistence.
    @Transactional
    public ShortenUrlResponse createShortUrl(ShortenUrlRequest request) {
        String originalUrl = validateOriginalUrl(request.getOriginalUrl());

        // Duplicate URL handling: if the same destination is submitted again, return the existing code.
        Optional<UrlMapping> existingMapping = urlMappingRepository.findByOriginalUrl(originalUrl);
        if (existingMapping.isPresent()) {
            return new ShortenUrlResponse(existingMapping.get().getShortCode());
        }

        String shortCode = resolveShortCode(request.getCustomAlias());

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping.setShortCode(shortCode != null ? shortCode : "");

        UrlMapping savedMapping;
        try {
            savedMapping = urlMappingRepository.save(mapping);
        } catch (DataIntegrityViolationException e) {
            // Race condition: another thread inserted the same URL between our check and insert.
            // Fetch the winning mapping and return its short code.
            Optional<UrlMapping> raceConditionMapping = urlMappingRepository.findByOriginalUrl(originalUrl);
            if (raceConditionMapping.isPresent()) {
                return new ShortenUrlResponse(raceConditionMapping.get().getShortCode());
            }
            throw new IllegalArgumentException("Unexpected race condition: URL was inserted but not found", e);
        }

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

    // Custom aliases are validated to stay unique and predictable. A duplicate alias is rejected
    // rather than silently overwriting an existing mapping.
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
            URI uri = URI.create(originalUrl);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("Invalid URL");
            }
            uri.toURL();
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
