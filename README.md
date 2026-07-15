# URL Shortener

## Overview
A simple Spring Boot URL shortener that stores mappings in H2 and redirects short codes to their original URLs.

## Tech Stack
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Spring Validation

## Setup
1. Clone the project.
2. Run the app with:
   ```bash
   ./gradlew bootRun
   ```
3. The app uses a file-based H2 database at `./data/urlshortner`.
4. Open the H2 console at `http://localhost:8080/h2-console`.

## API Endpoints
### POST /shorten
Creates a short URL mapping.

Request:
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

Use the exact value returned in `shortCode` for the redirect request.

### GET /{code}
Redirects to the original URL for the given short code.

Example:
```bash
GET /my-link
```

## Design Decisions
- Base62 is used for compact, URL-safe short codes.
- Duplicate URLs return the existing short code instead of creating a new mapping.
- Custom aliases are rejected if they already exist.
- Invalid or empty URLs return HTTP 400.

## AI Usage
AI was used to scaffold the Spring Boot structure, implement the CRUD-style flow, and add validation and tests. Manual decisions were made around the simple domain model, the Base62 approach, and the handling of duplicate URLs and custom aliases.