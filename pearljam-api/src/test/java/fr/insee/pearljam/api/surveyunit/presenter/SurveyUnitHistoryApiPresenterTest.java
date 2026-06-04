package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitHistoryResponse;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCommunication;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitHistoryApiPresenterTest {


    @Test
    void should_map_full_history_correctly() {

        SurveyUnitHistory history = new SurveyUnitHistory(
                "su1",
                "survey unit",
                List.of(new SurveyUnitState(10000000L, StateType.INS)),
                List.of(new SurveyUnitCommunication(10000000L, CommunicationType.REMINDER, CommunicationRequestReason.REFUSAL))
        );

        SurveyUnitHistoryApiPresenter presenter = new SurveyUnitHistoryApiPresenter();
        SurveyUnitHistoryResponse result = presenter.present(history);

        assertThat(result.surveyUnitId()).isEqualTo("su1");

        assertThat(result.surveyUnitDisplayName())
                .isEqualTo("survey unit");

        assertThat(result.states())
                .hasSize(1);

        assertThat(result.states().getFirst().type())
                .isEqualTo(StateType.INS.name());

        assertThat(result.communications())
                .hasSize(1);

        assertThat(result.communications().getFirst().type())
                .isEqualTo("REMINDER");
    }

    @Test
    void should_handle_null_fields() {

        SurveyUnitHistory history = new SurveyUnitHistory(
                "su1",
                null,
                null,
                null
        );
        SurveyUnitHistoryApiPresenter presenter = new SurveyUnitHistoryApiPresenter();
        SurveyUnitHistoryResponse result = presenter.present(history);

        assertThat(result.surveyUnitDisplayName()).isNull();
        assertThat(result.states()).isEmpty();
        assertThat(result.communications()).isEmpty();
    }


}
