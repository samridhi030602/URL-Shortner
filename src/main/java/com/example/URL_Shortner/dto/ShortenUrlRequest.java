package com.example.URL_Shortner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Request payload for the shorten endpoint.
public class ShortenUrlRequest {

    @NotBlank(message = "originalUrl is required")
    @Pattern(regexp = "^(https?|ftp)://.+", message = "Invalid URL")
    private String originalUrl;

    @Size(max = 50, message = "customAlias must be at most 50 characters")
    private String customAlias;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}
