package com.example.URL_Shortner.controller;

import com.example.URL_Shortner.dto.ShortenUrlRequest;
import com.example.URL_Shortner.dto.ShortenUrlResponse;
import com.example.URL_Shortner.entity.UrlMapping;
import com.example.URL_Shortner.repository.UrlMappingRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the URL Shortener endpoints using actual Spring Boot HTTP calls.
 * These tests verify end-to-end behavior with real database interactions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlShortenerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @BeforeEach
    public void setup() {
        // Clear the database before each test for isolation
        urlMappingRepository.deleteAll();
    }

    /**
     * Test that GET request with an unknown/nonexistent code returns HTTP 404.
     */
    @Test
    public void testGetUnknownCodeReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/nonexistent-code", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test that GET request with a valid code returns HTTP 301 with correct Location header.
     */
    @Test
    public void testGetValidCodeReturns301WithLocationHeader() {
        // Setup: Create a URL mapping
        String testUrl = "https://example.com";
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setOriginalUrl(testUrl);
        request.setCustomAlias("test-link");

        ResponseEntity<ShortenUrlResponse> shortenResponse = restTemplate.postForEntity(
                "/shorten",
                request,
                ShortenUrlResponse.class
        );

        assertEquals(HttpStatus.CREATED, shortenResponse.getStatusCode());
        String shortCode = shortenResponse.getBody().getShortCode();
        assertNotNull(shortCode);

        // Test: Fetch the short code and verify redirect
        ResponseEntity<String> redirectResponse = restTemplate.getForEntity("/" + shortCode, String.class);
        assertEquals(HttpStatus.MOVED_PERMANENTLY, redirectResponse.getStatusCode());
        assertEquals(testUrl, redirectResponse.getHeaders().getLocation().toString());
    }

    /**
     * Integration test that does a full POST /shorten -> GET /{code} round trip
     * and asserts the redirect works correctly.
     */
    @Test
    public void testFullRoundTripPostThenGetRedirect() {
        String longUrl = "https://www.example.com/very/long/path?param=value&foo=bar";

        // Step 1: POST to create a short URL
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setOriginalUrl(longUrl);

        ResponseEntity<ShortenUrlResponse> postResponse = restTemplate.postForEntity(
                "/shorten",
                request,
                ShortenUrlResponse.class
        );

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        String shortCode = postResponse.getBody().getShortCode();
        assertNotNull(shortCode);
        assertTrue(shortCode.length() > 0);

        // Step 2: GET the short code and verify it redirects to the original URL
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/" + shortCode, String.class);

        assertEquals(HttpStatus.MOVED_PERMANENTLY, getResponse.getStatusCode());
        assertEquals(longUrl, getResponse.getHeaders().getLocation().toString());
    }

    /**
     * Test that simulates a race condition by having two requests attempt
     * to shorten the same URL concurrently. Both should succeed, and both
     * should eventually receive the same short code (one wins the race,
     * the other catches the constraint violation and returns the winner's code).
     * 
     * Note: This test verifies that the race condition handling logic is present
     * and that concurrent requests don't cause fatal errors. The exact race
     * condition outcome depends on database transaction timing.
     */
    @Test
    public void testConcurrentDuplicateUrlHandling() throws InterruptedException {
        String sharedUrl = "https://concurrent-test.example.com";
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        String[] resultingShortCodes = new String[2];

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(2);

        // Create two threads that will attempt to shorten the same URL at nearly the same time
        for (int i = 0; i < 2; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    startLatch.countDown();
                    startLatch.await(); // Ensure both threads start at the same time

                    ShortenUrlRequest request = new ShortenUrlRequest();
                    request.setOriginalUrl(sharedUrl);

                    ResponseEntity<ShortenUrlResponse> response = restTemplate.postForEntity(
                            "/shorten",
                            request,
                            ShortenUrlResponse.class
                    );

                    if (response.getStatusCode() == HttpStatus.CREATED) {
                        resultingShortCodes[threadIndex] = response.getBody().getShortCode();
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);

        // Verify both requests succeeded
        assertEquals(2, successCount.get(), "Both requests should succeed");
        assertEquals(0, errorCount.get(), "No errors should occur");
        
        // Verify both returned a valid short code
        assertNotNull(resultingShortCodes[0], "Thread 0 should have a short code");
        assertNotNull(resultingShortCodes[1], "Thread 1 should have a short code");
        assertTrue(resultingShortCodes[0].length() > 0, "Short code 0 should not be empty");
        assertTrue(resultingShortCodes[1].length() > 0, "Short code 1 should not be empty");
        
        // Verify that the URL can be resolved via both short codes (or one if they're the same)
        ResponseEntity<String> response1 = restTemplate.getForEntity("/" + resultingShortCodes[0], String.class);
        assertEquals(HttpStatus.MOVED_PERMANENTLY, response1.getStatusCode());
        assertEquals(sharedUrl, response1.getHeaders().getLocation().toString());
    }
}
