package com.example.URL_Shortner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.URL_Shortner.dto.ShortenUrlRequest;
import com.example.URL_Shortner.dto.ShortenUrlResponse;
import com.example.URL_Shortner.entity.UrlMapping;
import com.example.URL_Shortner.repository.UrlMappingRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    private ShortenUrlRequest request;

    @BeforeEach
    void setUp() {
        request = new ShortenUrlRequest();
        request.setOriginalUrl("https://example.com");
    }

    @Test
    void shouldCreateShortUrlForValidRequest() {
        UrlMapping savedMapping = new UrlMapping();
        savedMapping.setId(1L);
        savedMapping.setOriginalUrl("https://example.com");
        savedMapping.setShortCode("1");

        when(urlMappingRepository.findByOriginalUrl("https://example.com")).thenReturn(Optional.empty());
        when(urlMappingRepository.save(org.mockito.ArgumentMatchers.any(UrlMapping.class))).thenReturn(savedMapping);

        ShortenUrlResponse response = urlShortenerService.createShortUrl(request);

        assertEquals("1", response.getShortCode());
    }

    @Test
    void shouldReturnExistingShortCodeForDuplicateUrl() {
        UrlMapping existingMapping = new UrlMapping();
        existingMapping.setShortCode("abc123");
        existingMapping.setOriginalUrl("https://example.com");

        when(urlMappingRepository.findByOriginalUrl("https://example.com")).thenReturn(Optional.of(existingMapping));

        ShortenUrlResponse response = urlShortenerService.createShortUrl(request);

        assertEquals("abc123", response.getShortCode());
    }

    @Test
    void shouldRejectInvalidUrl() {
        request.setOriginalUrl("not-a-valid-url");

        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.createShortUrl(request));
    }

    @Test
    void shouldRejectDuplicateAlias() {
        request.setCustomAlias("existing");

        when(urlMappingRepository.findByOriginalUrl("https://example.com")).thenReturn(Optional.empty());
        when(urlMappingRepository.existsByShortCode("existing")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.createShortUrl(request));
    }

    @Test
    void shouldCreateShortUrlWithHyphenatedCustomAlias() {
        request.setCustomAlias("YT-search");

        UrlMapping savedMapping = new UrlMapping();
        savedMapping.setId(16L);
        savedMapping.setOriginalUrl("https://example.com");
        savedMapping.setShortCode("YT-search");

        when(urlMappingRepository.findByOriginalUrl("https://example.com")).thenReturn(Optional.empty());
        when(urlMappingRepository.existsByShortCode("YT-search")).thenReturn(false);
        when(urlMappingRepository.save(org.mockito.ArgumentMatchers.any(UrlMapping.class))).thenReturn(savedMapping);

        ShortenUrlResponse response = urlShortenerService.createShortUrl(request);

        assertEquals("YT-search", response.getShortCode());
    }

    @Test
    void shouldCreateShortUrlWithUnderscoreCustomAlias() {
        request.setCustomAlias("my_short_link");

        UrlMapping savedMapping = new UrlMapping();
        savedMapping.setId(17L);
        savedMapping.setOriginalUrl("https://example.com");
        savedMapping.setShortCode("my_short_link");

        when(urlMappingRepository.findByOriginalUrl("https://example.com")).thenReturn(Optional.empty());
        when(urlMappingRepository.existsByShortCode("my_short_link")).thenReturn(false);
        when(urlMappingRepository.save(org.mockito.ArgumentMatchers.any(UrlMapping.class))).thenReturn(savedMapping);

        ShortenUrlResponse response = urlShortenerService.createShortUrl(request);

        assertEquals("my_short_link", response.getShortCode());
    }
}
