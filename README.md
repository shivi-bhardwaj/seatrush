# seatrush

![CI](https://github.com/shivi-bhardwaj/seatrush/actions/workflows/ci.yml/badge.svg)

A flash-sale ticket booking platform: 50,000 users trying to book 2,000 concert seats the moment the sale opens at 10:00:00.

This is a **learning project**. The scenario forces the hard problems — double booking, retries on flaky networks, holds that expire, traffic spikes — and each one is solved and then *proven* with a test or experiment.

## Stack

- Java 25 (Amazon Corretto), Spring Boot 4.1, Maven wrapper
- PostgreSQL 17, schema managed by Flyway (Hibernate only validates it)
- Docker Compose for local infrastructure, Testcontainers for tests
- GitHub Actions CI
- Planned: Redis, RabbitMQ, then AWS (SQS and friends)

## Running locally

Prerequisites: JDK 25 and Docker.

```powershell
.\mvnw spring-boot:run
```

Spring Boot's Docker Compose support starts Postgres from `compose.yaml` automatically.

Notes:

- **Postgres runs on host port 5433**, not 5432, to avoid clashing with a locally installed Postgres. Database, user: `seatrush`, password: `secret` (local only).
- **The app runs in UTC** (`-Duser.timezone=UTC`, set in `pom.xml` for both the app and tests). Some JVMs report legacy zone IDs such as `Asia/Calcutta`, which the Postgres 17 image rejects.
- If JDK 25 is not your default, point `JAVA_HOME` at it for the current terminal only, e.g. on Windows:
  ```powershell
  $env:JAVA_HOME = (Get-ChildItem "C:\Program Files\Amazon Corretto" -Directory -Filter "jdk25*").FullName
  ```
- To reset the local database: `docker compose down -v` (also needed after changing `POSTGRES_*` settings, which Postgres reads only on first start).

Run the build and tests:

```powershell
.\mvnw verify
```

## Domain model

| Entity | What it is |
|---|---|
| `Venue`, `Seat` | A physical venue and its seats |
| `Event` | A concert at a venue, with a sale opening time |
| `PriceTier` | A price level for an event (e.g. VIP, General) |
| `EventSeat` | Per-event inventory: one row per seat per event, `AVAILABLE` / `HELD` / `BOOKED`, optimistic locking |
| `Booking` | One user's attempt to buy seats: `HELD` → `CONFIRMED` / `EXPIRED` / `CANCELLED`, with an idempotency key |
| `BookingItem` | A seat in a booking, with the price snapshotted at booking time |

## Design decisions

- **Idempotency key on bookings** — the client sends a UUID per "Book" tap; retries after a timeout hit a unique constraint instead of creating a second booking.
- **Hold expiry stored, not computed** — `hold_expires_at` is saved on the booking, so changing the hold duration never shortens holds already given out.
- **Price snapshot** — `BookingItem` copies the price at booking time; later price changes don't rewrite what a customer paid.
- **Double-booking guard** — `EventSeat` status plus `@Version` (optimistic locking); `booking_item` is history, so a seat may appear in many bookings over time.
