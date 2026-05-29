# Role

You're a senior software craft developper respecting SOLID, KISS, DRY.
Make sure your code is easy to read and has no cognitive complexity.

# Java Spring Boot Skill

Build production-ready Spring Boot applications with modern best practices.

## ⚡ MANDATORY RULES

1. ❌ NEVER do N+1 query
2. ✅ ALWAYS respect SOLID principles

## Overview

This skill covers Spring Boot development including REST APIs, security configuration, data access, actuator monitoring, and cloud integration. Follows Spring Boot 3.x patterns with emphasis on production readiness.

## When to Use This Skill

Use when you need to:
- Create REST APIs with Spring MVC/WebFlux
- Configure Spring Security (OAuth2, JWT)
- Set up database access with Spring Data
- Enable monitoring with Actuator
- Integrate with Spring Cloud

## Topics Covered

### Spring Boot Core
- Auto-configuration and starters
- Application properties and profiles
- Bean lifecycle and configuration
- DevTools and hot reload

### REST API Development
- @RestController and @RequestMapping
- Request/response handling
- Validation with Bean Validation
- Exception handling with @ControllerAdvice

### Spring Security
- SecurityFilterChain configuration
- OAuth2 and JWT authentication
- Method security (@PreAuthorize)
- CORS and CSRF configuration

### Spring Data JPA
- Repository pattern
- Query methods and @Query
- Pagination and sorting
- Auditing and transactions

### Actuator & Monitoring
- Health checks and probes
- Metrics with Micrometer
- Custom endpoints
- Prometheus integration

## Quick Reference

```java
// REST Controller
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitClosingController {

    private final DomainFeaturePort domainFeaturePort;

    // Example Post
    @PostMapping(API_PATH)
    public ResponseEntity<Void> addClosingCauseToMultipleSurveyUnits(
            @RequestBody @Valid ObjectRequest request) {

        domainFeaturePort.domainMethod(request.getObject1(), request.getObject2());
        return ResponseEntity.ok().build();
    }
}
```

## Common Patterns

### Validation Patterns
```java
public record CreateUserRequest(
    @NotBlank @Size(max = 100) String name,
    @Email @NotBlank String email,
    @NotNull @Min(18) Integer age
) {}
```

## Troubleshooting

### Common Issues

| Problem | Cause | Solution |
|---------|-------|----------|
| Bean not found | Missing @Component | Add annotation or @Bean |
| Circular dependency | Constructor injection | Use @Lazy or refactor |
| Slow startup | Heavy auto-config | Exclude unused starters |


### Debug Checklist
```
□ Check /actuator/conditions
□ Verify active profiles
□ Review security filter chain
□ Check bean definitions
□ Test health endpoints
```
