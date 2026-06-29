package fr.insee.pearljam.jms.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.modelefiliere.EventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperConfigurationTest {

    private final ObjectMapper objectMapper = new MapperConfiguration().objectMapper();

    @Test
    @DisplayName("Should deserialize an event payload containing a java.time.Instant date")
    void shouldDeserializeInstantInPayload() {
        String json = """
                {
                  "eventType": "QUESTIONNAIRE_INIT",
                  "payload": {
                    "leafStates": [
                      { "date": "2026-06-29T09:42:08.220Z" }
                    ]
                  }
                }""";

        EventDto eventDto = assertDoesNotThrow(() -> objectMapper.readValue(json, EventDto.class));

        assertEquals(
                Instant.parse("2026-06-29T09:42:08.220Z"),
                eventDto.getPayload().getLeafStates().get(0).getDate());
    }
}
