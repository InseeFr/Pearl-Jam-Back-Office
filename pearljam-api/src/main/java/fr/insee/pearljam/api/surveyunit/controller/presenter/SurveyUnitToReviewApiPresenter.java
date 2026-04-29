package fr.insee.pearljam.api.surveyunit.controller.presenter;

import fr.insee.pearljam.api.reporting.response.SurveyUnitToReviewDto;
import fr.insee.pearljam.api.reporting.response.SurveyUnitToReviewResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToReviewPresenter;
import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitToReview;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurveyUnitToReviewApiPresenter implements
        SurveyUnitToReviewPresenter<SurveyUnitToReviewResponse> {

    @Value("${application.external.service.datacollection-ui-url}")
    private String datacollectionUiUrl;

    @Override
    public SurveyUnitToReviewResponse present(Page<SurveyUnitToReview> surveyUnits) {
        List<SurveyUnitToReviewDto> dtos = surveyUnits.getContent().stream()
                .map(this::mapToDto)
                .toList();

        return new SurveyUnitToReviewResponse(
                dtos,
                surveyUnits.getNumber(),
                surveyUnits.getSize(),
                surveyUnits.getTotalElements(),
                surveyUnits.getTotalPages()
        );
    }

    private SurveyUnitToReviewDto mapToDto(SurveyUnitToReview surveyUnit) {
        return new SurveyUnitToReviewDto(
                surveyUnit.id(),
                surveyUnit.campaignLabel(),
                surveyUnit.contactOutcome(),
                surveyUnit.interviewerName(),
                surveyUnit.viewed(),
                datacollectionUiUrl + "/review/interrogations/" + surveyUnit.id(),
                surveyUnit.lastComment()
        );
    }
}