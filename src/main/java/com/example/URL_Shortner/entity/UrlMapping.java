package com.example.URL_Shortner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

// JPA entity mapped to the database table.
@Entity
@Table(name = "url_mapping")
public class UrlMapping {

    // Primary key generated automatically by the database.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Required field for the original destination URL.
    @Column(nullable = false, unique = true)
    private String originalUrl;

    // Unique short code used to look up the mapping.
    @Column(nullable = false, unique = true)
    private String shortCode;

    // Timestamp set once when the entity is first persisted.
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Automatically assign the creation timestamp before insert.
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
