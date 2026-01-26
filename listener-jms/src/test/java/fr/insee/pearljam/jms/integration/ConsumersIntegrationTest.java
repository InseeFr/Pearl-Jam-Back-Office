package fr.insee.pearljam.jms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.pearljam.JMSApplication;
import fr.insee.pearljam.api.domain.OtherModeQuestionnaireState;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.OtherModeQuestionnaireRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.db.events.InboxJpaRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.PersonDB;
import jakarta.jms.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JMSApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "feature.multimode.subscriber.enabled=true",
        "feature.multimode.topic=multimode_events_test",
        "spring.docker.compose.file=../api/compose.yml",
        "spring.docker.compose.lifecycle-management=start-and-stop"
})
class ConsumersIntegrationTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InboxJpaRepository inboxRepository;

    @Autowired
    private OtherModeQuestionnaireRepository otherModeQuestionnaireRepository;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    private JmsTemplate jmsTemplate;

    @BeforeEach
    void setUp() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);

        // Clean up before each test
        inboxRepository.deleteAll();
        otherModeQuestionnaireRepository.deleteAll();
    }

    @Test
    @DisplayName("Should process QUESTIONNAIRE_INIT event and create OtherModeQuestionnaireState")
    void shouldProcessQuestionnaireInitEvent() throws Exception {
        // Given
        String surveyUnitId = getExistingSurveyUnitId();
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_INIT, surveyUnitId);
        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then
        waitForInboxEntry(correlationId, 10);

        List<OtherModeQuestionnaireState> states = waitForOtherModeQuestionnaireState(surveyUnitId, 10);
        assertFalse(states.isEmpty(), "OtherModeQuestionnaireState should be created");

        OtherModeQuestionnaireState state = states.stream()
                .filter(s -> "QUESTIONNAIRE_INIT".equals(s.getState()))
                .findFirst()
                .orElse(null);
        assertNotNull(state, "QUESTIONNAIRE_INIT state should exist");
    }

    @Test
    @DisplayName("Should process QUESTIONNAIRE_VALIDATED event and create OtherModeQuestionnaireState")
    void shouldProcessQuestionnaireValidatedEvent() throws Exception {
        // Given
        String surveyUnitId = getExistingSurveyUnitId();
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED, surveyUnitId);
        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then
        waitForInboxEntry(correlationId, 10);

        List<OtherModeQuestionnaireState> states = waitForOtherModeQuestionnaireState(surveyUnitId, 10);
        assertFalse(states.isEmpty(), "OtherModeQuestionnaireState should be created");

        OtherModeQuestionnaireState state = states.stream()
                .filter(s -> "QUESTIONNAIRE_VALIDATED".equals(s.getState()))
                .findFirst()
                .orElse(null);
        assertNotNull(state, "QUESTIONNAIRE_VALIDATED state should exist");
    }

    @Test
    @DisplayName("Should process QUESTIONNAIRE_COMPLETED event and create OtherModeQuestionnaireState")
    void shouldProcessQuestionnaireCompletedEvent() throws Exception {
        // Given
        String surveyUnitId = getExistingSurveyUnitId();
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED, surveyUnitId);
        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then
        waitForInboxEntry(correlationId, 10);

        List<OtherModeQuestionnaireState> states = waitForOtherModeQuestionnaireState(surveyUnitId, 10);
        assertFalse(states.isEmpty(), "OtherModeQuestionnaireState should be created");

        OtherModeQuestionnaireState state = states.stream()
                .filter(s -> "QUESTIONNAIRE_COMPLETED".equals(s.getState()))
                .findFirst()
                .orElse(null);
        assertNotNull(state, "QUESTIONNAIRE_COMPLETED state should exist");
    }

    @Test
    @DisplayName("Should process QUESTIONNAIRE_UPDATED event and create OtherModeQuestionnaireState")
    void shouldProcessQuestionnaireUpdatedEvent() throws Exception {
        // Given
        String surveyUnitId = getExistingSurveyUnitId();
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED, surveyUnitId);
        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then
        waitForInboxEntry(correlationId, 10);

        List<OtherModeQuestionnaireState> states = waitForOtherModeQuestionnaireState(surveyUnitId, 10);
        assertFalse(states.isEmpty(), "OtherModeQuestionnaireState should be created");

        OtherModeQuestionnaireState state = states.stream()
                .filter(s -> "QUESTIONNAIRE_UPDATED".equals(s.getState()))
                .findFirst()
                .orElse(null);
        assertNotNull(state, "QUESTIONNAIRE_UPDATED state should exist");
    }

    @Test
    @DisplayName("Should process QUESTIONNAIRE_LEAF_STATES_UPDATED event and create OtherModeQuestionnaireState")
    void shouldProcessQuestionnaireLeafStatesUpdatedEvent() throws Exception {
        // Given
        String surveyUnitId = getExistingSurveyUnitId();
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED, surveyUnitId);
        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then
        waitForInboxEntry(correlationId, 10);

        List<OtherModeQuestionnaireState> states = waitForOtherModeQuestionnaireState(surveyUnitId, 10);
        assertFalse(states.isEmpty(), "OtherModeQuestionnaireState should be created");

        OtherModeQuestionnaireState state = states.stream()
                .filter(s -> "QUESTIONNAIRE_LEAF_STATES_UPDATED".equals(s.getState()))
                .findFirst()
                .orElse(null);
        assertNotNull(state, "QUESTIONNAIRE_LEAF_STATES_UPDATED state should exist");
    }

    @Test
    @Transactional
    @DisplayName("Should process MULTIMODE_MOVED event and update SurveyUnit")
    void shouldProcessMultimodeMovedEvent() throws Exception {
        // Given
        String surveyUnitId = getExistingSurveyUnitId();
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.MULTIMODE_MOVED, surveyUnitId);
        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then
        waitForInboxEntry(correlationId, 10);
        waitForSurveyUnitUpdate(surveyUnitId, 10);

        // Verify the survey unit data in a separate transactional context
        verifyMovedSurveyUnitData(surveyUnitId);
    }

    void verifyMovedSurveyUnitData(String surveyUnitId) {
        SurveyUnit updatedSurveyUnit = surveyUnitRepository.findById(surveyUnitId)
                .orElseThrow(() -> new AssertionError("SurveyUnit should exist"));

        assertTrue(updatedSurveyUnit.isPriority(), "SurveyUnit priority should be true");

        if (updatedSurveyUnit.getIdentification() != null) {
            assertTrue(updatedSurveyUnit.getIdentification().getDemenagementWeb(),
                    "Identification demenagementWeb should be true");
        }

        // Verify that only one person exists with firstName="PRENOM" and lastName="NOM"
        assertEquals(1, updatedSurveyUnit.getPersons().size(),
                "Should have exactly one person");
        PersonDB person = updatedSurveyUnit.getPersons().iterator().next();
        assertEquals("PRENOM", person.getFirstName(),
                "Person firstName should be PRENOM");
        assertEquals("NOM", person.getLastName(),
                "Person lastName should be NOM");
    }

    @Test
    @DisplayName("Should process multiple different event types")
    void shouldProcessMultipleDifferentEventTypes() throws Exception {
        // Given
        String surveyUnitId = getExistingSurveyUnitId();

        UUID correlationId1 = UUID.randomUUID();
        EventDto event1 = createEventDto(correlationId1, EventDto.EventTypeEnum.QUESTIONNAIRE_INIT, surveyUnitId);

        UUID correlationId2 = UUID.randomUUID();
        EventDto event2 = createEventDto(correlationId2, EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED, surveyUnitId);

        // When
        jmsTemplate.convertAndSend("multimode_events_test", objectMapper.writeValueAsString(event1));
        jmsTemplate.convertAndSend("multimode_events_test", objectMapper.writeValueAsString(event2));

        // Then
        waitForInboxEntry(correlationId1, 10);
        waitForInboxEntry(correlationId2, 10);

        List<OtherModeQuestionnaireState> states = waitForOtherModeQuestionnaireState(surveyUnitId, 10);

        boolean hasInit = states.stream().anyMatch(s -> "QUESTIONNAIRE_INIT".equals(s.getState()));
        boolean hasValidated = states.stream().anyMatch(s -> "QUESTIONNAIRE_VALIDATED".equals(s.getState()));

        assertTrue(hasInit, "QUESTIONNAIRE_INIT state should exist");
        assertTrue(hasValidated, "QUESTIONNAIRE_VALIDATED state should exist");
    }

    private String getExistingSurveyUnitId() {
        return surveyUnitRepository.findAll().stream()
                .findFirst()
                .map(SurveyUnit::getId)
                .orElseThrow(() -> new RuntimeException("No SurveyUnit found in database. Ensure test data is loaded."));
    }

    private EventDto createEventDto(UUID correlationId, EventDto.EventTypeEnum eventType, String interrogationId) {
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(interrogationId);

        EventDto eventDto = new EventDto();
        eventDto.setCorrelationId(correlationId);
        eventDto.setEventType(eventType);
        eventDto.setPayload(payload);

        return eventDto;
    }

    private void waitForInboxEntry(UUID correlationId, int maxSeconds) throws InterruptedException {
        int attempts = maxSeconds * 2;
        for (int i = 0; i < attempts; i++) {
            if (inboxRepository.existsById(correlationId)) {
                return;
            }
            Thread.sleep(500);
        }
        fail("Inbox entry not found for correlation ID: " + correlationId);
    }

    private List<OtherModeQuestionnaireState> waitForOtherModeQuestionnaireState(String surveyUnitId, int maxSeconds) throws InterruptedException {
        int attempts = maxSeconds * 2;
        for (int i = 0; i < attempts; i++) {
            List<OtherModeQuestionnaireState> states = otherModeQuestionnaireRepository.findAll().stream()
                    .filter(s -> s.getSurveyUnit() != null && surveyUnitId.equals(s.getSurveyUnit().getId()))
                    .toList();
            if (!states.isEmpty()) {
                return states;
            }
            Thread.sleep(500);
        }
        return List.of();
    }

    private void waitForSurveyUnitUpdate(String surveyUnitId, int maxSeconds) throws InterruptedException {
        int attempts = maxSeconds * 2;
        for (int i = 0; i < attempts; i++) {
            SurveyUnit surveyUnit = surveyUnitRepository.findById(surveyUnitId).orElse(null);
            if (surveyUnit != null && surveyUnit.isPriority()) {
                return;
            }
            Thread.sleep(500);
        }
        fail("SurveyUnit was not updated with priority=true within " + maxSeconds + " seconds");
    }
}
