# Boardgame Booking

A small distributed booking system for a board-game rental service. The project is built as a multi-service backend with a lightweight web UI and focuses on reservation consistency, asynchronous communication, and reactive I/O.

## Architecture

```text
                         ┌──────────────┐
                         │   Frontend   │
                         │ React + Vite │
                         └──────┬───────┘
                                │
               ┌────────────────┼────────────────┐
               │                │                │
               ▼                ▼                ▼
        ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
        │   Catalog   │  │ Reservation │  │    Order    │
        │ Java/WebFlux│  │ Java/WebFlux│  │Kotlin/WebFlux│
        └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
               │                │                │
               ▼                ▼                ▼
          PostgreSQL          Redis         PostgreSQL
                                │                │
                                └──── RabbitMQ ──┘
```

### Services

- **Catalog service** — board-game catalog backed by PostgreSQL through R2DBC.
- **Reservation service** — short-lived reservation holds stored in Redis; designed to prevent two clients from acquiring the same hold concurrently.
- **Order service** — order lifecycle implemented with Kotlin, WebFlux and R2DBC; integrates with RabbitMQ for asynchronous events.
- **Frontend** — React/Vite UI served through Nginx in Docker.

## Tech stack

**Backend**

- Java 21
- Kotlin
- Spring Boot 3.5
- Spring WebFlux / Project Reactor
- Spring Data R2DBC
- PostgreSQL
- Redis (reactive client)
- RabbitMQ / Spring AMQP
- Liquibase
- OpenAPI / Springdoc
- Gradle

**Infrastructure / UI**

- Docker & Docker Compose
- Kubernetes manifests
- React + Vite
- Nginx
- RedisInsight
- MailHog

## Why these technologies?

### Redis for reservation holds

A reservation is temporary state rather than a permanent order. Redis makes it possible to keep holds lightweight and expire them automatically. Atomic `SET if absent` semantics are used so concurrent requests cannot successfully acquire the same hold.

### RabbitMQ for service communication

Reservation and order workflows do not need every step to be coupled through synchronous HTTP calls. RabbitMQ provides asynchronous communication and keeps service boundaries explicit.

### Reactive stack

Catalog and order persistence use R2DBC, while HTTP APIs are based on WebFlux. This keeps database and network I/O non-blocking throughout the main request flow.

## Local run

The easiest way to run the whole system is Docker Compose.

```bash
docker compose up --build
```

After startup:

| Component | Address |
|---|---|
| Frontend | http://localhost:3000 |
| Catalog service | http://localhost:8081 |
| Reservation service | http://localhost:8082 |
| Order service | http://localhost:8083 |
| RedisInsight | http://localhost:5540 |
| MailHog | http://localhost:8025 |
| PostgreSQL | localhost:5433 |
| RabbitMQ | localhost:5673 |

Stop the stack with:

```bash
docker compose down
```

## Repository layout

```text
boardgame_booking/
├── frontend/
├── services/
│   ├── boardbox_catalog/
│   ├── reservation/
│   └── order-service/
├── k8s/
├── db/
├── docker-compose.yaml
├── build.gradle
└── settings.gradle
```

## Engineering topics demonstrated

This repository is primarily a backend/system-design project rather than a UI showcase. It demonstrates:

- service decomposition and explicit bounded responsibilities;
- reactive HTTP and database access;
- Redis-based temporary locking/holds;
- asynchronous messaging with RabbitMQ;
- transactional persistence and event-driven order processing;
- schema migrations with Liquibase;
- containerized local infrastructure;
- basic Kubernetes deployment manifests.

## Status

Pet/portfolio project. The focus is on backend architecture and distributed-system patterns rather than production-ready UX or operational hardening.
