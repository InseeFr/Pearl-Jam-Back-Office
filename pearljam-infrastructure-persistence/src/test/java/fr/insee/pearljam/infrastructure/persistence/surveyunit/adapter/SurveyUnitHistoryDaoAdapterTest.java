package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationHistoryDto;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyUnitHistoryDaoAdapterTest {

    @Mock
    SurveyUnitRepository surveyUnitRepository;

    @Mock
    StateRepository stateRepository;

    @Mock
    CommunicationRequestRepository communicationRepository;

    @InjectMocks
    SurveyUnitHistoryDaoAdapter adapter;

    @Test
    void should_build_history_correctly() {

        SurveyUnitDB db = new SurveyUnitDB();
        db.setId("su1");
        db.setDisplayName("Survey 1");

        when(surveyUnitRepository.findById("su1"))
                .thenReturn(Optional.of(db));

        when(stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc("su1"))
                .thenReturn(List.of(
                        new StateDto(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), StateType.ANV)
                ));

        when(communicationRepository.findAllDtoBySurveyUnitIdOrderByDateAsc("su1"))
                .thenReturn(List.of(
                        new CommunicationHistoryDto(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "REMINDER","UNREACHABLE")
                ));

        SurveyUnitHistory result = adapter.findSurveyUnitHistory("su1");

        assertThat(result)
                .isNotNull();

        assertThat(result.surveyUnitId())
                .isEqualTo("su1");

        assertThat(result.surveyUnitDisplayName())
                .isEqualTo("Survey 1");

        assertThat(result.states())
                .isNotNull()
                .hasSize(1);

        assertThat(result.communications())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void should_return_empty_history_when_no_states_and_no_communications() {

        SurveyUnitDB db = new SurveyUnitDB();
        db.setId("su1");
        db.setDisplayName("Survey 1");

        when(surveyUnitRepository.findById("su1"))
                .thenReturn(Optional.of(db));

        when(stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc("su1"))
                .thenReturn(List.of());

        when(communicationRepository.findAllDtoBySurveyUnitIdOrderByDateAsc("su1"))
                .thenReturn(List.of());

        SurveyUnitHistory result = adapter.findSurveyUnitHistory("su1");

        assertThat(result).isNotNull();

        assertThat(result.surveyUnitId()).isEqualTo("su1");
        assertThat(result.surveyUnitDisplayName()).isEqualTo("Survey 1");

        assertThat(result.states())
                .isNotNull()
                .isEmpty();

        assertThat(result.communications())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void should_handle_empty_states() {

        SurveyUnitDB db = new SurveyUnitDB();
        db.setId("su1");
        db.setDisplayName("Survey 1");

        when(surveyUnitRepository.findById("su1"))
                .thenReturn(Optional.of(db));

        when(stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc("su1"))
                .thenReturn(List.of());

        when(communicationRepository.findAllDtoBySurveyUnitIdOrderByDateAsc("su1"))
                .thenReturn(List.of(
                        new CommunicationHistoryDto(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "REMINDER", "REFUSAL")
                ));

        SurveyUnitHistory result = adapter.findSurveyUnitHistory("su1");

        assertThat(result.states()).isEmpty();
        assertThat(result.communications()).hasSize(1);
    }
}
