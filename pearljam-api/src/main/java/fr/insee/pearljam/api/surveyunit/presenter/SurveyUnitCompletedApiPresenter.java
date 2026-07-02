package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCompletedPageResponse;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCompletedPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurveyUnitCompletedApiPresenter implements SurveyUnitCompletedPresenter<SurveyUnitCompletedPageResponse> {

    @Value("${application.external.service.datacollection-ui-url}")
    private String datacollectionUiUrl;

    @Override
    public SurveyUnitCompletedPageResponse present(Page<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits) {

        List<SurveyUnitCompletedPageResponse.SurveyUnitCompletedResponse> content =
        surveyUnits.stream().map(su ->
                new SurveyUnitCompletedPageResponse.SurveyUnitCompletedResponse(
                        su.surveyUnitId(),
                        su.surveyUnitDisplayName(),
                        getInterviewerLabel(su),
                        su.endDate(),
                        su.contactOutcome() != null
                                ? ContactOutcomeType.valueOf(su.contactOutcome())
                                : null,
                        su.closingCauseType() != null
                                ? ClosingCauseType.valueOf(su.closingCauseType())
                                : null,
                        su.viewed(),
                        datacollectionUiUrl + "/review/interrogations/" + su.surveyUnitId(),
                        su.comment())).toList();

        return new SurveyUnitCompletedPageResponse(
                                content,
                                surveyUnits.getNumber(),
                                surveyUnits.getSize(),
                                surveyUnits.getTotalElements(),
                                surveyUnits.getTotalPages()
                        );
    }

    @Override
    public SurveyUnitCompletedPageResponse empty() {
        return null;
    }
}

