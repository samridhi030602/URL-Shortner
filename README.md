# URL Shortener

## Approach
This is a simple URL shortening service built as part of an engineering exercise.

## Tech Stack
- Spring Boot (familiarity and fast iteration)
- Spring Data JPA
- H2 Database (zero setup, easy evaluation)

## Goals
- Focus on clean design and decision making
- Keep implementation simple and extensible

## API Design
- POST /shorten → create short URL
- GET /{code} → redirect

## Core Requirements
- Unique short codes
- URL validation
- Handle duplicates
- Support custom aliases

## High-Level Design
Client → Controller → Service → Repository → DB

## Data Model
Each URL mapping stores:
- original URL
- generated short code
- timestamp

Using auto-increment ID for deterministic short code generation.