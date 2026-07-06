# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Startup Instructions

Before doing anything else:
Read this CLAUDE.md.
Read every file in the context/ directory, Use them as the authoritative source of project context.
Build an understanding of the project.
Tell me when you're ready. Don't make any changes yet.

## Project Overview

This is **Fuel-flow** (also called "Fuel-station application"), an operations management and reporting backend for oil companies that own fuel stations. Full context lives in `context/project-overview.md` — read it before working on domain logic. Key points:

- A **Company** owns many **Stations**. Each Station has **Pumps**, **Attendants** (staff), **PosTerminals**, and **StationInventory**.
- Attendants are assigned to a pump daily (not fixed) and clock in/out with opening/closing meter readings — no shift model.
- A **PumpAudit** records opening/closing readings per pump per session; a **FuelSale** records the actual sale (litres, price, payment) tied to a `PumpAudit`. `ReconciliationService` compares audited dispensed volume against recorded sales to detect variance (see `service/ReconciliationService.java`).
- This app is designed to eventually integrate with an external microservice system called **CAMS**, which already handles authentication and POS-terminal payments (card/transfer). Many entities carry CAMS reference fields (`merchantId`, `outletId`, `posTerminalId`, `transactionReference`) that mirror CAMS identifiers rather than local foreign keys — this is intentional, not dead data.
- Cash payments (not routed through CAMS) must also be accounted for in reporting — keep this in mind when touching `PaymentMethod` / transaction logic.

## Build & Run

Standard Maven project, no wrapper script (`mvnw`) present — use a system-installed `mvn`. Java 25, Spring Boot 3.5.0.

```
mvn compile                          # build
mvn spring-boot:run                  # run the app (port 8080 default)
mvn test                             # run tests
mvn test -Dtest=ClassName#method     # run a single test
mvn package                          # build jar (target/inventory_tracker-1.0-SNAPSHOT.jar)
```

There are currently no test source files under `src/test/java` — when adding the first tests, `spring-boot-starter-test` is already a dependency, so no POM changes are needed.

Swagger/OpenAPI UI is available at `/swagger-ui/**` (springdoc) once the app is running, and is permitted without auth (see Security below).

Database config is in `src/main/resources/application.properties` (PostgreSQL, `ddl-auto=update`). It currently contains a hardcoded local username/password for a local Postgres instance — this is dev-only config committed to the repo, not a secret to reuse or propagate elsewhere.

## Architecture

Layered Spring Boot architecture under `org.inventory_tracker`:

- `controller/` — `@RestController`s. Thin: validate (`@Valid`), delegate to a service, wrap in `ResponseEntity`. Authorization is declared here via `@PreAuthorize("hasRole(...)")` / `hasAnyRole(...)`, matching the coarse-grained rules also enforced in `SecurityConfig`.
- `service/` — business logic. Services depend on repositories and mappers directly (there is no separate "use case" layer).
- `repository/` — Spring Data JPA interfaces. No business logic; custom query methods only (e.g. `existsByRole`, `sumLitresByPumpAudit`).
- `entity/` — JPA entities. Most extend `BaseEntity` (adds `createdAt`/`updatedAt` via Hibernate `@CreationTimestamp`/`@UpdateTimestamp`). IDs are `IDENTITY`-generated `Long`.
- `dto/request/` and `dto/response/` — API-facing DTOs, one per operation/entity. Entities are never returned directly from controllers.
- `config/mapper/` — MapStruct mappers (`@Mapper(componentModel = "spring")`) converting between entities and DTOs. A `ModelMapper` bean also exists (`config/AppConfig.java`) — prefer MapStruct for new mappers to match the dominant pattern; only fall back to ModelMapper where MapStruct's compile-time generation doesn't fit.
- `enums/` — domain enums (`Role`, `ProductType`, `PaymentMethod`, `TransactionType`, `TransactionStatus`, `InventoryMovementType`, `ShiftType`).
- `security/` + `config/SecurityConfig.java` — stateless JWT auth. `JwtAuthenticationFilter` reads the `Authorization: Bearer` header, resolves the user via `AdminDetailsService`, and validates the token via `JwtService`. `SecurityConfig` permits `/auth/**` and Swagger paths, restricts `/companies/**` to `SUPER_ADMIN` and `/stations/**` to `SUPER_ADMIN`/`ADMIN`, and requires authentication on everything else.
- `config/SuperAdminInitializer.java` — a `CommandLineRunner` that seeds a default `SUPER_ADMIN` Admin account on startup if none exists (hardcoded credentials — dev convenience, not a pattern to replicate for real user provisioning).

Auth/role model: `Role` enum is `SUPER_ADMIN`, `ADMIN`, `STAFF`. Only `Admin` implements the security `UserDetails` flow today (`AdminDetailsService`); `Attendant` is a separate entity (`username`/`pin`) not currently wired into Spring Security.

## Project Conventions

These are pulled from `context/coding-standards.md` and apply repo-wide:

- Controllers stay thin; business logic belongs in services; repositories hold no business logic.
- Clean Architecture boundaries: controllers must not access repositories directly; DTOs must not contain business logic.
- Use MapStruct or ModelMapper for entity↔DTO mapping (see `config/mapper/` for existing examples) — don't hand-roll mapping code.
- Follow the existing package structure (`controller` / `service` / `repository` / `entity` / `dto` / `enums` / `config`).
- Never modify database migrations unless explicitly instructed.
- Do not rename public REST endpoints.
- Do not introduce new dependencies without asking first.
- Don't make unrelated refactors while completing a task.

## Working Style (from `context/ai-interactions.md`)

- Explain reasoning before making significant changes; if uncertain, ask rather than assume.
- Give a numbered implementation plan before coding, and show a diff summary before making changes.
- Prefer modifying existing code over introducing new abstractions.
- Cite the specific files a conclusion is based on.
- Keep responses concise, no emojis.
- Review your own changes before considering a task complete.
