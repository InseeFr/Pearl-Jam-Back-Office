package fr.insee.pearljam.jms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.pearljam.JMSApplication;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.db.events.InboxDB;
import fr.insee.pearljam.infrastructure.db.events.InboxJpaRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.PersonDB;
import jakarta.jms.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
class MultimodeSubscriberIntegrationTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InboxJpaRepository inboxRepository;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    @BeforeEach
    void setUp() {
        // Clean up inbox before each test
        inboxRepository.deleteAll();
    }

    @Test
    void shouldSaveMessageToInbox() throws Exception {
        // Given: Create a test event
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = new EventDto();
        eventDto.setCorrelationId(correlationId);
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);

        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When: Send message to topic
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then: Wait for message to be processed and verify in inbox
        InboxDB inboxEntry = waitForInboxEntry(correlationId, 10);

        assertNotNull(inboxEntry, "Inbox entry should exist");
        assertEquals(correlationId, inboxEntry.getId(), "Inbox ID should match correlation ID");
        assertNotNull(inboxEntry.getPayload(), "Payload should not be null");
        assertNotNull(inboxEntry.getCreatedDate(), "Created date should not be null");

        // Verify payload content
        assertEquals("QUESTIONNAIRE_INIT", inboxEntry.getPayload().get("eventType").asText());
    }

    @Test
    void shouldIgnoreDuplicateMessages() throws Exception {
        // Given: Create a test event
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = new EventDto();
        eventDto.setCorrelationId(correlationId);
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);

        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When: Send the same message twice to topic
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Wait for first message to be processed
        InboxDB firstEntry = waitForInboxEntry(correlationId, 10);
        assertNotNull(firstEntry, "First message should be saved");

        // Send duplicate
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Wait a bit to ensure duplicate would be processed if it wasn't ignored
        Thread.sleep(2000);

        // Then: Verify only one entry exists
        assertEquals(1, inboxRepository.count(), "Only one entry should exist (duplicate ignored)");

        InboxDB inboxEntry = inboxRepository.findById(correlationId).orElse(null);
        assertNotNull(inboxEntry, "Inbox entry should still exist");
    }

    @Test
    void shouldSaveMultipleMessagesToInbox() throws Exception {
        // Given: Create multiple test events
        List<UUID> correlationIds = new ArrayList<>();
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);

        for (int i = 0; i < 3; i++) {
            UUID correlationId = UUID.randomUUID();
            correlationIds.add(correlationId);

            EventDto eventDto = new EventDto();
            eventDto.setCorrelationId(correlationId);
            eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED);

            String jsonMessage = objectMapper.writeValueAsString(eventDto);

            // When: Send messages to topic
            jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);
        }

        // Then: Wait for all messages to be processed
        for (UUID correlationId : correlationIds) {
            InboxDB inboxEntry = waitForInboxEntry(correlationId, 15);
            assertNotNull(inboxEntry, "Inbox entry should exist for correlation ID: " + correlationId);
            assertEquals(correlationId, inboxEntry.getId(), "Inbox ID should match correlation ID");
            assertNotNull(inboxEntry.getPayload(), "Payload should not be null");
        }

        // Verify total count
        assertEquals(3, inboxRepository.count(), "Exactly 3 entries should exist in inbox");
    }

    @Test
    @Transactional
    void shouldSaveMultimodeMovedMessageToInbox() throws Exception {
        // Given: Get an existing survey unit from the database
        String surveyUnitId = getExistingSurveyUnitId();

        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = new EventDto();
        eventDto.setCorrelationId(correlationId);
        eventDto.setEventType(EventDto.EventTypeEnum.MULTIMODE_MOVED);

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(surveyUnitId);
        eventDto.setPayload(payload);

        String jsonMessage = objectMapper.writeValueAsString(eventDto);

        // When: Send message to topic
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("multimode_events_test", jsonMessage);

        // Then: Wait for message to be processed and verify in inbox
        InboxDB inboxEntry = waitForInboxEntry(correlationId, 10);

        assertNotNull(inboxEntry, "Inbox entry should exist");
        assertEquals(correlationId, inboxEntry.getId(), "Inbox ID should match correlation ID");
        assertNotNull(inboxEntry.getPayload(), "Payload should not be null");
        assertNotNull(inboxEntry.getCreatedDate(), "Created date should not be null");

        // Verify payload content
        assertEquals("MULTIMODE_MOVED", inboxEntry.getPayload().get("eventType").asText());
        assertEquals(surveyUnitId, inboxEntry.getPayload().get("payload").get("interrogationId").asText());

        // Verify that the survey unit has been updated in the database
        waitForSurveyUnitUpdate(surveyUnitId, 10);

        // Verify the survey unit data in a separate transactional context
        verifySurveyUnitData(surveyUnitId);
    }

    void verifySurveyUnitData(String surveyUnitId) {
        SurveyUnit updatedSurveyUnit = surveyUnitRepository.findById(surveyUnitId)
                .orElseThrow(() -> new AssertionError("SurveyUnit should exist"));

        assertTrue(updatedSurveyUnit.isPriority(), "SurveyUnit priority should be true");

        // Verify that only one person exists with firstName="PRENOM" and lastName="NOM"
        assertEquals(1, updatedSurveyUnit.getPersons().size(),
                "Should have exactly one person");
        PersonDB person = updatedSurveyUnit.getPersons().iterator().next();
        assertEquals("PRENOM", person.getFirstName(),
                "Person firstName should be PRENOM");
        assertEquals("NOM", person.getLastName(),
                "Person lastName should be NOM");
    }

    private String getExistingSurveyUnitId() {
        return surveyUnitRepository.findAll().stream()
                .findFirst()
                .map(SurveyUnit::getId)
                .orElseThrow(() -> new RuntimeException("No SurveyUnit found in database. Ensure test data is loaded."));
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

    /**
     * Helper method to wait for an inbox entry to appear in the database.
     * Polls every 500ms for up to maxSeconds.
     */
    private InboxDB waitForInboxEntry(UUID correlationId, int maxSeconds) throws InterruptedException {
        int attempts = maxSeconds * 2; // Poll every 500ms
        for (int i = 0; i < attempts; i++) {
            InboxDB entry = inboxRepository.findById(correlationId).orElse(null);
            if (entry != null) {
                return entry;
            }
            Thread.sleep(500);
        }
        return null;
    }
}
