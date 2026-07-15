# URL Shortener

## Overview
This project is a simple URL shortening service built with Spring Boot, Spring Data JPA, and H2. It currently supports creating short links, storing them in a database, and redirecting users to the original URL.

## Tech Stack
- Spring Boot
- Spring Data JPA
- H2 Database
- Spring Web

## Current Features
- Create a short URL via POST /shorten
- Redirect to the original URL via GET /{code}
- Persist URL mappings in the H2 database
- Validate incoming URLs
- Reuse the existing short code for the same original URL
- Reject duplicate custom aliases
- Generate short codes using Base62 encoding derived from the database ID

## API Endpoints

### POST /shorten
Creates a new short URL mapping.

Request body:
```json
{
  "originalUrl": "https://example.com",
  "customAlias": "my-link"
}
```

Response:
```json
{
  "shortCode": "my-link"
}
```

If no custom alias is provided, the service generates a short code automatically.

### GET /{code}
Redirects to the original URL for the provided short code.

Example:
```bash
GET /abc123
```

If the code does not exist, the API returns HTTP 404.

## Project Structure
- Controller: handles HTTP requests and responses
- Service: contains the business logic
- Repository: interacts with the database through JPA
- Entity: stores the URL mapping data

## Data Model
Each URL mapping stores:
- originalUrl
- shortCode
- createdAt
- id (auto-generated primary key)

## Notes
- This is an initial version of the service.
- Duplicate handling and alias behavior are intentionally simple and readable.
- The short code generation is based on Base62 encoding of the persisted database ID.