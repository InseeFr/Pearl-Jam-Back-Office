package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitAssignedPageResponse;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitAssignedResponse;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SurveyUnitAssignedApiPresenter implements
    SurveyUnitAssignedPresenter<SurveyUnitAssignedPageResponse> {


    @Override
    public SurveyUnitAssignedPageResponse present(Page<SurveyUnitAssigned> surveyUnits) {
        List<SurveyUnitAssignedResponse> dtos = surveyUnits.getContent().stream()
            .map(this::mapToDto)
            .toList();

        return new SurveyUnitAssignedPageResponse(
            dtos,
            surveyUnits.getNumber(),
            surveyUnits.getSize(),
            surveyUnits.getTotalElements(),
            surveyUnits.getTotalPages()
        );
    }

    private SurveyUnitAssignedResponse mapToDto(SurveyUnitAssigned surveyUnit) {
        return new SurveyUnitAssignedResponse(
            surveyUnit.surveyUnitId(),
            surveyUnit.surveyUnitDisplayName(),
            SurveyUnitAssignedPresenter.buildInterviewerLabel(surveyUnit),
            surveyUnit.ssech(),
            surveyUnit.location(),
            surveyUnit.city(),
            toStateType(surveyUnit.questionnaireState()),
            toClosingCauseType(surveyUnit.closingCause())
        );
    }

    StateType toStateType(String value) {
        return value == null ? null : StateType.valueOf(value);
    }

    ClosingCauseType toClosingCauseType(String value) {
        return value == null ? null : ClosingCauseType.valueOf(value);
    }

}
