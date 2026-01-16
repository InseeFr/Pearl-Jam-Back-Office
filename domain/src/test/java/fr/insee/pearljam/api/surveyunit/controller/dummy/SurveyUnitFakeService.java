package fr.insee.pearljam.api.surveyunit.controller.dummy;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.state.StateDto;
import fr.insee.pearljam.api.dto.surveyunit.*;
import fr.insee.pearljam.api.service.SurveyUnitService;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitCreationDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitInterviewerResponseDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.exception.PersonNotFoundException;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

public class SurveyUnitFakeService implements SurveyUnitService {

    @Setter
    private boolean shouldThrowSurveyUnitException = false;

    @Setter
    private boolean shouldThrowPersonException = false;

    @Getter
    private SurveyUnitUpdateDto surveyUnitUpdated = null;

    @Override
    public List<SurveyUnitDto> getSurveyUnitDto(String userId, Boolean extended) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public SurveyUnitDetailDto updateSurveyUnit(String userId, String surveyUnitId, SurveyUnitUpdateDto surveyUnitUpdateDto) throws SurveyUnitNotFoundException, PersonNotFoundException {
        if(shouldThrowSurveyUnitException) {
            throw new SurveyUnitNotFoundException(surveyUnitId);
        }

        if(shouldThrowPersonException) {
            throw new PersonNotFoundException();
        }
        surveyUnitUpdated = surveyUnitUpdateDto;
        return null;
    }

    @Override
    public Set<SurveyUnitCampaignDto> getSurveyUnitByCampaign(String userId, String id, String state) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public HttpStatus addStateToSurveyUnit(String listSU, StateType state) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public List<StateDto> getListStatesBySurveyUnitId(String suId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public SurveyUnit getSurveyUnit(String surveyUnitId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public List<SurveyUnitCampaignDto> getClosableSurveyUnits(HttpServletRequest request, String userId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public HttpStatus updateSurveyUnitViewed(String userId, String surveyUnitId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public HttpStatus closeSurveyUnit(String surveyUnitId, ClosingCauseType closingCause) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public HttpStatus updateClosingCause(String surveyUnitId, ClosingCauseType closingCause) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public List<SurveyUnit> getSurveyUnitIdByOrganizationUnits(List<String> lstOuId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public Response createSurveyUnits(List<SurveyUnitCreationDto> surveyUnits) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public Response createSurveyUnitInterviewerLinks(List<SurveyUnitInterviewerLinkDto> surveyUnitInterviewerLink) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public boolean checkHabilitationInterviewer(String userId, String id) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public boolean checkHabilitationReviewer(String userId, String id) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public void delete(String surveyUnitId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public void saveSurveyUnitToTempZone(String id, String userId, JsonNode surveyUnit) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public List<SurveyUnitTempZone> getAllSurveyUnitTempZone() {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public boolean canBeSeenByInterviewer(String suId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public List<String> getAllIds() {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public List<String> getAllIdsByCampaignId(String campaignId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public List<String> getAllIdsByInterviewerId(String interviewerId) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public void removeInterviewerLink(List<String> ids) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public SurveyUnitInterviewerResponseDto buildSurveyUnitInterviewerResponse(SurveyUnit surveyUnit) {
        return null;
    }

    @Override
    public SurveyUnitInterviewerResponseDto getSurveyUnitInterviewerDetail(String userId, String surveyUnitId) {
        return null;
    }

    @Override
    public SurveyUnitInterviewerResponseDto getSurveyUnitDetail(String surveyUnitId) {
        return null;
    }
}
