package com.example.URL_Shortner.dto;

// Response payload returned by the initial shorten endpoint.
public class ShortenUrlResponse {

    private String shortCode;

    public ShortenUrlResponse() {
    }

    public ShortenUrlResponse(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}
