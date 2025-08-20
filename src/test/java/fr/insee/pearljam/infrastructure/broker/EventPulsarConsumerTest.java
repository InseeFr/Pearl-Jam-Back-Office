package fr.insee.pearljam.infrastructure.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPulsarConsumerTest {

    @Mock
    private MessageConsumer consumerA;

    @Mock
    private MessageConsumer consumerB;

    private EventPulsarConsumer sut;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        sut = new EventPulsarConsumer(List.of(consumerA, consumerB));
    }

    private String buildBusinessJson(String type, String interrogationId) throws Exception {
        // {"type":"QUESTIONNAIRE_INIT","payload":{"interrogationId":"20"}}
        var business = String.format("""
            {"type":%s,"payload":{"interrogationId":%s}}
            """,
                mapper.writeValueAsString(type),
                mapper.writeValueAsString(interrogationId)
        );
        return business;
    }

    private String buildEnvelopeWithAfter(String businessJson) throws Exception {
        String payloadAsJsonString = mapper.writeValueAsString(businessJson); // ajoute les quotes + échappe
        String id = UUID.randomUUID().toString();
        return """
            {
              "before": null,
              "after": {
                "id": %s,
                "payload": %s,
                "created_date": 0
              },
              "op": "c"
            }
            """.formatted(
                mapper.writeValueAsString(id),
                payloadAsJsonString
        );
    }

    // ---------- tests ----------

    @Test
    void should_dispatch_to_matching_consumer() throws Exception {
        String business = buildBusinessJson("QUESTIONNAIRE_INIT", "20");
        String envelope = buildEnvelopeWithAfter(business);

        when(consumerA.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(true);
        when(consumerB.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(false);

        sut.listen(envelope, "{\"id\":\"" + UUID.randomUUID() + "\"}");

        verify(consumerA, times(1)).shouldConsume("QUESTIONNAIRE_INIT");
        verify(consumerA, times(1)).consume(eq("QUESTIONNAIRE_INIT"), any());
        verifyNoMoreInteractions(consumerA);

        verify(consumerB, times(1)).shouldConsume("QUESTIONNAIRE_INIT");
        verify(consumerB, never()).consume(anyString(), any());
    }

    @Test
    void should_dispatch_to_all_matching_consumers() throws Exception {
        String business = buildBusinessJson("QUESTIONNAIRE_INIT", "42");
        String envelope = buildEnvelopeWithAfter(business);

        when(consumerA.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(true);
        when(consumerB.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(true);

        sut.listen(envelope, null);

        verify(consumerA).consume(eq("QUESTIONNAIRE_INIT"), any());
        verify(consumerB).consume(eq("QUESTIONNAIRE_INIT"), any());
    }

    @Test
    void should_do_nothing_when_no_consumer_matches() throws Exception {
        String business = buildBusinessJson("QUESTIONNAIRE_INIT", "99");
        String envelope = buildEnvelopeWithAfter(business);

        when(consumerA.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(false);
        when(consumerB.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(false);

        sut.listen(envelope, null);

        verify(consumerA, never()).consume(anyString(), any());
        verify(consumerB, never()).consume(anyString(), any());
    }

    @Test
    void should_skip_when_after_missing() throws Exception {
        String envelope = """
            { "before": null, "after": null, "op": "d" }
            """;

        sut.listen(envelope, null);

        verifyNoInteractions(consumerA, consumerB);
    }

    @Test
    void should_skip_when_envelope_is_json_null() {
        String envelope = "null";

        sut.listen(envelope, null);

        verifyNoInteractions(consumerA, consumerB);
    }

    @Test
    void should_skip_when_business_payload_is_invalid_json() throws Exception {
        String invalidBusinessJson = "{\"type\":\"QUESTIONNAIRE_INIT\",\"payload\":{invalid}";
        String envelope = buildEnvelopeWithAfter(invalidBusinessJson);

        sut.listen(envelope, null);

        verifyNoInteractions(consumerA, consumerB);
    }

    @Test
    void should_pass_correct_type_to_consume() throws Exception {
        String business = buildBusinessJson("QUESTIONNAIRE_INIT", "20");
        String envelope = buildEnvelopeWithAfter(business);

        when(consumerA.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(true);

        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);

        sut.listen(envelope, null);

        verify(consumerA).consume(typeCaptor.capture(), any());
        org.junit.jupiter.api.Assertions.assertEquals("QUESTIONNAIRE_INIT", typeCaptor.getValue());
    }
}