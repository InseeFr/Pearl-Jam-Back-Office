# Repository Guidelines

## Project Structure & Module Organization
This project is a Spring Boot 3.5.x API (Java 21) built with Maven.
- `src/main/java/fr/insee/pearljam` contains 3 main modules:
  - `api`: REST controllers, DTOs, Spring configuration, JPA repositories, legacy services.
  - `domain`: business models/services with ports (`port/userside`, `port/serverside`).
  - `infrastructure`: adapters, persistence entities/DAO adapters, security and mail integrations.
- `src/main/resources` contains runtime config:
  - `application.yml`, `application-default.yml`, `application-docker.yml`
  - `logback-spring.xml`
  - Liquibase changelogs under `db/changelog/` and roots in `db/master.xml`, `db/integration-*.xml`.
- `src/test/java` contains unit, integration (`*IT`), architecture, and Cucumber tests.
- `src/test/resources` contains test profiles (`application-auth.yml`, `application-noauth.yml`, `application-demo.yml`) and Cucumber features (`features/*.feature`).
- `container/`, `Dockerfile`, `compose.yml` are used for local stack (PostgreSQL + Keycloak).

## Build, Test, and Development Commands
Use JDK 21. Prefer Maven Wrapper when available.
- `./mvnw clean install`: full build + all tests.
- `./mvnw test`: run Surefire tests.
- `./mvnw -Dtest=MyTest test`: run a specific test class.
- `./mvnw spring-boot:run`: run the API locally.
- `./mvnw clean package`: build JAR in `target/`.
- `java -jar target/pearljam-back-office-*.jar`: run packaged app.
- `./mvnw -Pcoverage test`: generate JaCoCo report in `target/site/jacoco/`.

## Coding Style & Naming Conventions
Follow existing conventions in touched files only.
- Keep existing indentation/style; do not reformat unrelated code.
- Package root is `fr.insee.pearljam...`.
- Class names use `UpperCamelCase`; methods/fields use `lowerCamelCase`.
- Respect established naming even when legacy typos exist (example: `bussinessrules` package).
- For domain code, keep port/adapter boundaries explicit (`domain/.../port`, `infrastructure/.../adapter`).

## Testing Guidelines
Testing stack: JUnit 5, Spring Boot Test, Cucumber, ArchUnit.
- Unit tests mostly end with `*Test`.
- Integration tests end with `*IT`.
- Cucumber uses `*TestRunner` and feature files under `src/test/resources/features`.
- Surefire also includes explicit auth suites: `TestAuthKeyCloak` and `TestNoAuth` (see `pom.xml`).

## Commit & Pull Request Guidelines
Use Conventional Commits with short imperative summaries.
- Preferred prefixes: `feat:`, `fix:`, `chore:`, `docs:`, `test:`.
- Keep commit scope focused and avoid mixing refactor + behavior change without need.
- Reference ticket/issue in PR and document API contract changes (request/response examples if relevant).

## Security & Configuration Tips
- Never commit secrets. Use `.env` and local overrides for credentials/tokens.
- Liquibase runs at startup; register schema changes through `src/main/resources/db/master.xml`.
- OIDC and Swagger are feature-flagged via `feature.*` properties.
- Swagger UI is available only when enabled (`feature.swagger.enabled=true`), with Springdoc path configured in `application.yml`.
