package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCommunicationResponse;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitHistoryResponse;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitStateResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitHistoryPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCommunication;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurveyUnitHistoryApiPresenter implements SurveyUnitHistoryPresenter<SurveyUnitHistoryResponse> {


    public SurveyUnitHistoryResponse present(SurveyUnitHistory h) {

        return new SurveyUnitHistoryResponse(
                h.surveyUnitId(),
                h.surveyUnitDisplayName(),
                mapStates(h.states()),
                mapCommunications(h.communications())
        );
    }


    private List<SurveyUnitStateResponse> mapStates(List<SurveyUnitState> states) {
        if (states == null) return List.of();

        return states.stream()
                .map(s -> new SurveyUnitStateResponse(
                        s.type().name(),
                        s.date()
                ))
                .toList();
    }

    private List<SurveyUnitCommunicationResponse> mapCommunications(List<SurveyUnitCommunication> communications) {
        if (communications == null) return List.of();

        return communications.stream()
                .map(c -> new SurveyUnitCommunicationResponse(
                        c.type().name(),
                        c.date(),
                        c.reason().name()
                ))
                .toList();
    }
}
