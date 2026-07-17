# URL Shortener Service

A simple URL shortening microservice built using Spring Boot.

---

## 🧠 Approach & Thought Process

The goal of this assignment was not just to build a working service, but to demonstrate engineering thinking, trade-offs, and decision-making.

I approached the problem incrementally:

1. Started with a basic working flow (generate short code + store mapping)
2. Improved the implementation using Base62 encoding
3. Considered edge cases and extensibility

---

## ⚙️ Tech Stack Choices

### Spring Boot
- Chosen for rapid development and ease of building REST APIs
- Reduces boilerplate and speeds up implementation

### Spring Data JPA
- Simplifies database operations
- Avoids writing raw SQL and improves maintainability

### H2 Database (File-based)
- No setup required for evaluation
- Lightweight and easy to use
- Supports persistence across restarts (if configured)

---

## 🔗 API Design

### 1. Create Short URL

POST /shorten

Request:
```json
{
  "originalUrl": "https://example.com",
  "customAlias": "optional"
}
```

Response:
```json
{
  "shortCode": "abc123"
}
```

---

### 2. Redirect to Original URL

GET /{shortCode}

- Redirects the user to the original URL

---

## Setup
1. Clone the project.
2. Ensure you have Java 17 or later installed.
3. Run the app with:
   ```bash
   ./gradlew bootRun
   ```
4. Run tests with:
   ```bash
   ./gradlew test
   ```
5. The app uses a file-based H2 database at `./data/urlshortner`. This directory is automatically created on first run and recreated fresh if deleted.
6. Open the H2 console at `http://localhost:8080/h2-console`.



## 🔢 Short Code Generation Strategy

### Why Base62?

Base62 encoding (a-z, A-Z, 0-9) was chosen because:

- Generates short and compact URLs
- URL-safe (no special characters)
- More readable than hashes
- More efficient than UUIDs

### Alternatives Considered

- UUID: Easy to implement, but too long
- Hashing (MD5/SHA): Deterministic, but collision risk and longer output
- Base62: Short, readable, and a good balance of simplicity and usability

Final decision: Base62 provides the best balance between compactness and usability.

---

## ⚖️ Trade-offs & Design Decisions

### Simplicity vs Scalability

- Current solution is simple and single-node
- Not designed for distributed systems
- Prioritized clarity over scalability

### Database Choice

- Used H2 for simplicity
- Trade-off: not suitable for production-scale systems

### Custom Alias Handling

- Allows user-defined short codes
- Requires handling collisions
- Trade-off: flexibility vs additional validation logic

---

## ⚠️ Edge Cases Considered

- Invalid or malformed URLs
- Duplicate short codes
- Custom alias conflicts
- Missing or unknown short codes (404)

---

## 🧪 Testing

Basic validation includes:

- URL creation flow
- Redirect functionality
- Handling invalid inputs

---

## 🤖 Use of AI

AI tools were used to:

- Generate initial boilerplate code
- Assist with API structure
- Help with Base62 encoding approach

However:

- All design decisions were made independently
- Code was reviewed and modified where needed
- Trade-offs were consciously evaluated

---

## 🔧 How to Run

### Prerequisites

- Java 17+
- Gradle

### Steps

```bash
git clone <repo-url>
cd URL-Shortner
./gradlew bootRun
```

---

## 🌐 Access

- API Base URL: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- H2 Console: http://localhost:8080/h2-console

---

## 📌 Future Improvements

- Add caching (e.g., Redis) for faster lookups
- Implement rate limiting
- Add analytics (click tracking)
- Improve test coverage
- Containerize using Docker
- Add logging and monitoring

---

## 🏁 Conclusion

This project focuses on:

- Clean and simple architecture
- Practical decision-making
- Thoughtful trade-offs

Instead of over-engineering, the focus was on:

✔ Simplicity
✔ Clarity
✔ Correctness
