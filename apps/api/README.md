# Portfolio API

This repository contains the backend API for my personal engineering portfolio.

The API is intentionally small, explicit, and boring.  
It focuses on correctness, security, and maintainability rather than feature breadth.

It exists to support a limited set of real user-facing needs (contact form, health checks, basic metadata), and to demonstrate how I design backend systems.

---

## Overview

- Built with **Spring Boot 4**
- Kotlin-first codebase
- Clear separation between web, security, domain, and infrastructure layers
- Explicit security model (API keys + scopes)
- Minimal dependencies, no magic abstractions

The goal is not to build a generic platform, but to show **how I reason about backend architecture and testing**.

---

## Tech stack

### Backend
- **Spring Boot 4**
- **Kotlin**
- Spring WebMVC
- Spring Security
- Spring Data JPA
- Flyway (database migrations)

### Database
- PostgreSQL
- UUID primary keys
- Auditing fields (`createdAt`, `createdBy`, etc.)

### Security
- API key authentication
- Scoped access control
- Explicit allowlist for public endpoints
- Stateless request handling

---

## API design principles

- Explicit contracts over conventions
- No hidden behavior
- Fail fast on invalid input
- Security enforced at the edge (filters)
- Internal services remain framework-agnostic where possible

Endpoints are intentionally few and well-defined.

---

## Authentication & authorization

### API keys
- API keys are **never stored in plaintext**
- Keys are hashed using SHA-256 before persistence
- Clients send the raw key via the `X-API-KEY` header

### Scopes
- Each endpoint declares a required scope
- Requests without the correct scope are rejected with `403`
- Public endpoints explicitly opt out of authentication

This model keeps authorization logic:
- centralized
- testable
- easy to reason about

---

## Contact form flow

1. Request enters through a secured endpoint
2. API key + scope are validated
3. Honeypot field is checked
4. IP-based rate limiting is applied
5. Message is moderated
6. Email is sent via **Brevo**

External calls (email, moderation) are isolated behind services and mocked in tests.

---

## Configuration

### Environment variables

The following configuration values are required:

```bash
BREVO_API_KEY=
CONTACT_FROM_EMAIL=
CONTACT_TO_EMAIL=
CONTACT_FROM_NAME=
```

Secrets are **never committed** and are only read server-side.

---

## Testing philosophy

The test suite follows a strict test pyramid.

### Unit tests
- Pure functions
- Domain services
- Security utilities (hashing, scope resolution, rate limiting)
- No Spring context

### Slice tests
- `@WebMvcTest` for controllers and security filters
- Dependencies mocked explicitly
- No database, no JPA

### Integration tests
- `@SpringBootTest` + Testcontainers
- Real PostgreSQL
- Flyway migrations applied
- External integrations mocked

Tests are designed to be:
- fast
- deterministic
- meaningful

Coverage is not chased for its own sake; every test exists to protect real behavior.

---

## Running locally

### Prerequisites
- Java 21+
- Docker (for integration tests)
- PostgreSQL (optional, Testcontainers can be used)

### Run the application

```bash
./gradlew bootRun
```

### Run tests

```bash
./gradlew test
```

Integration tests will automatically start a PostgreSQL container when required.

---

## Architecture notes

- No shared mutable state
- No global exception swallowing
- Auditing is explicitly enabled only in JPA contexts
- MVC slice tests do not load persistence infrastructure
- Security behavior is tested independently from controllers

The codebase is structured to make unintended coupling obvious.

---

## License

This project is released under the **MIT License**.

You are free to reuse patterns or ideas, but the focus of this repository is educational rather than reusable infrastructure.
