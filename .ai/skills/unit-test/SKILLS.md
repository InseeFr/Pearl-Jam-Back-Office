# Queen-Back-Office Test Patterns Documentation

This document captures the test patterns and conventions used in the Queen-Back-Office project, specifically focusing on the patterns found in the queen-domain modules. These patterns serve as guidelines for LLM agents to generate consistent, high-quality tests.

## Test Structure Patterns

### Test Class Structure

```java
class ServiceNameTest {
    private ServiceName service;
    private DependencyRepositoryStub dependencyRepositoryStub;

    @BeforeEach
    void init() {
        dependancyRepositoryStub = new DependencyRepositoryStub();
        service = new ServiceName(dependencyRepositoryStub);
    }

    @Test
    @DisplayName("Descriptive test scenario description")
    void testFeatureScenario01() {
        // Test implementation
    }
}
```

### Test Method Patterns

#### Happy Path Testing
```java
@Test
@DisplayName("When [action], [expected positive outcome]")
void testFeatureSuccess01() {
    // Given - Setup test data
    dependancyRepositoryStub.setData(testData);

    // When - Execute method under test
    var result = service.methodUnderTest(params);

    // Then - Verify expected outcome
    assertThat(result).isEqualTo(expectedResult);
}
```

#### Exception Testing
```java
@Test
@DisplayName("When [action], throws exception if [condition]")
void testFeatureError01() {
    // Given - Setup error condition
    dependancyRepositoryStub.setData(null);

    // When/Then - Verify exception is thrown
    assertThatThrownBy(() -> service.methodUnderTest(params))
            .isInstanceOf(ExpectedException.class)
            .hasMessageContaining("expected message");
}
```

#### No-Exception Testing
```java
@Test
@DisplayName("When [action], if [condition], resume")
void testFeatureNoException01() {
    // Given - Setup valid condition

    // When/Then - Verify no exception is thrown
    assertThatCode(() -> service.methodUnderTest(params))
            .doesNotThrowAnyException();
}
```

### Common Test Scenarios

#### Repository Interaction Testing
```java
@Test
@DisplayName("On [action], verify [repository interaction]")
void testRepositoryInteraction01() {
    // Given
    String entityId = "test-id";

    // When
    service.methodUnderTest(entityId);

    // Then - Verify repository calls
    verify(repository).expectedMethod(entityId);
    verify(repository, never()).unexpectedMethod();
}
```

## Mocking Patterns

### Mockito Usage
```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private DependencyRepository dependencyRepository;

    @BeforeEach
    void setUp() {
        service = new Service(dependencyRepository);
    }

    @Test
    void testMethod() {
        // Given
        when(dependencyRepository.findById(anyString()))
                .thenReturn(Optional.of(testEntity));

        // When
        service.methodUnderTest("test-id");

        // Then
        verify(dependencyRepository).findById("test-id");
        verify(dependencyRepository, never()).delete(anyString());
    }
}
```

### Stub Repository Pattern
```java
// Using Stub repositories instead of mocks for complex scenarios
private CampaignDependancyRepositoryStub campaignRepository;

@BeforeEach
void init() {
    campaignRepository = new CampaignDependancyRepositoryStub();
    service = new MetadataApiService(campaignRepository);
}

@Test
void testWithdependancyRepositoryStub() {
    // Setup Stub repository state
    campaignRepository.setMetadata(metadata);

    // Test service behavior
    var result = service.getMetadataByQuestionnaireId("test-id");

    // Verify results
    assertThat(result).isEqualTo(metadata);
}
```

## Test Data Patterns

### Test Data Setup
```java
// From MetadataServiceTest.java
@BeforeEach
void init() {
    metadata = JsonNodeFactory.instance.objectNode();
    metadata.put("field", "value");
    campaignRepository = new CampaignDependancyRepositoryStub();
    metadataService = new MetadataApiService(campaignRepository);
}
```

### Parameterized Testing
```java
// From InterrogationApiServiceTest.java
@ParameterizedTest
@MethodSource("provideTestScenarios")
@DisplayName("Should handle [scenario] correctly")
void testParameterizedScenario(ScenarioInput input, ExpectedOutput output) {
    // Given
    setupTestData(input);

    // When
    var result = service.methodUnderTest(input);

    // Then
    assertThat(result).isEqualTo(output);
}
```

## Test Naming Conventions

1. **Test Class Names**: `[ServiceName]Test`
2. **Test Method Names**: `test[Feature][ScenarioNumber]` or descriptive camelCase
3. **Display Names**: "When [action], [expected outcome]" or "On [action], [expected behavior]"

Examples from the codebase:
- `testMetadataQuestionnaire01()`
- `testMetadataCampaign02()`
- `delete_should_delete_interrogations()`
- `test_campaign_existence_01()`

## Test Organization Patterns

1. **Given-When-Then** structure in test methods
2. **Arrange-Act-Assert** pattern
3. **Single responsibility** per test method
4. **Comprehensive coverage** of happy paths and error cases
5. **Clear separation** between test setup and execution

## Test Rules for LLM Agents

Based on the analysis of Queen-Back-Office test patterns, these rules should guide LLM agents:

### 1. Test Structure Rules

- Always use `@BeforeEach` for test setup and initialization
- Use `@DisplayName` with descriptive scenario names that follow "When [action], [expected outcome]" pattern
- Follow Given-When-Then or Arrange-Act-Assert pattern in test methods
- Keep test methods focused on single scenarios
- Use appropriate imports: `org.junit.jupiter.api.*` and `static org.assertj.core.api.Assertions.*`

### 2. Naming Rules

- **Test classes**: `[ServiceName]Test` (e.g., `MetadataServiceTest`)
- **Test methods**: Use either:
    - `test[Feature][ScenarioNumber]` pattern (e.g., `testMetadataQuestionnaire01`)
    - Descriptive camelCase names (e.g., `delete_should_delete_interrogations`)
- **Display names**: Use descriptive phrases like "When [action], [expected outcome]"

### 3. Assertion Rules

- Use AssertJ assertions exclusively (`assertThat`)
- Test both happy paths and error conditions comprehensively
- Verify exceptions with `assertThatThrownBy`
- Verify no exceptions with `assertThatCode`
- Use mock verification (`verify()`) for repository interactions when using Mockito
- For collections, use appropriate AssertJ collection assertions

### 4. Mocking Rules

- Use `@Mock` annotations for Mockito dependencies
- Use stub repositories for complex state management scenarios
- Verify mock interactions with `verify()` when testing specific method calls
- Use `when().thenReturn()` for setting up mock behavior

### 5. Test Coverage Rules

- Test all public methods of the service under test
- Cover both happy paths and all error conditions
- Test edge cases including null, empty, and boundary values
- Verify repository interactions and method calls
- Test data validation and exception handling
- Ensure comprehensive coverage of all branches and conditions

### 6. Test Data Rules

- Set up test data in `@BeforeEach` methods for reuse
- Use realistic but simple test data
- For JSON data, use `JsonNodeFactory.instance.objectNode()` for creating test JSON objects
- Keep test data focused and minimal for each test scenario

### 7. Test Quality Rules

- Each test should verify exactly one behavior or scenario
- Avoid test interdependencies - each test should be independent
- Use clear, descriptive names that document the test intent
- Follow SOLID principles in test design
- Keep tests DRY (Don't Repeat Yourself) but not at the cost of clarity
- Tests should be deterministic and repeatable

## Examples from Queen-Back-Office Codebase

### MetadataServiceTest.java Pattern

```java
@BeforeEach
void init() {
    metadata = JsonNodeFactory.instance.objectNode();
    metadata.put("field", "value");
    campaignRepository = new CampaignDependancyRepositoryStub();
    metadataService = new MetadataApiService(campaignRepository);
}

@Test
@DisplayName("When retrieving metadata for questionnaire, throws exception if metadata not found")
void testMetadataQuestionnaire01() {
    campaignRepository.setMetadata(null);
    assertThatThrownBy(() -> metadataService.getMetadataByQuestionnaireId(CampaignDependancyRepositoryStub.QUESTIONNAIRE_LINKED_ID))
            .isInstanceOf(EntityNotFoundException.class);
}

@Test
@DisplayName("When retrieving metadata for questionnaire, return metadata")
void testMetadataQuestionnaire02() {
    campaignRepository.setMetadata(metadata);
    assertThat(metadataService.getMetadataByQuestionnaireId(CampaignDependancyRepositoryStub.QUESTIONNAIRE_LINKED_ID))
            .isEqualTo(metadata);
}
```

## Integration with Existing Testing Standards

This testing pattern documentation complements the existing `.clinerules/testing.md` file by providing specific examples and patterns from the Queen-Back-Office project. The general testing principles from `testing.md` should be followed, with these patterns serving as concrete implementation guidelines.

## Best Practices Summary

1. **Consistency**: Follow established naming and structural patterns
2. **Clarity**: Use descriptive names and clear Given-When-Then structure
3. **Completeness**: Test both happy paths and all error conditions
4. **Maintainability**: Keep tests focused and independent
5. **Readability**: Use AssertJ for fluent, readable assertions
6. **Appropriate Mocking**: Choose between Stub and Mockito based on dependency complexity