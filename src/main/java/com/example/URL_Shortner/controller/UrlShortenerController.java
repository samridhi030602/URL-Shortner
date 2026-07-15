package com.example.URL_Shortner.controller;

import com.example.URL_Shortner.dto.ShortenUrlRequest;
import com.example.URL_Shortner.dto.ShortenUrlResponse;
import com.example.URL_Shortner.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Basic REST controller for the initial URL shortening flow.
@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    // Initial version of the endpoint; this will be improved later.
    @PostMapping("/shorten")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenUrlResponse shortenUrl(@RequestBody ShortenUrlRequest request) {
        return urlShortenerService.createShortUrl(request);
    }
}
