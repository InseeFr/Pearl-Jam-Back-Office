package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.api.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.domain.surveyunit.model.count.StateCount;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;

import java.util.List;
import java.util.Map;

public interface StateRepository {
    StateDto findFirstDtoBySurveyUnitOrderByDateDesc(SurveyUnitDB surveyUnit);

    List<StateDto> findAllDtoBySurveyUnitIdOrderByDateAsc(String suId);

    StateDto findFirstDtoBySurveyUnitIdOrderByDateDesc(String surveyUnitId);

    Map<String, Long> getStateCount(String campaignId, String interviewerId, List<String> ouIds, Long date);

    Map<String, Long> getStateCountSumByInterviewer(List<String> campaignIds, String interviewerId, List<String> ouIds, Long date);

    Map<String, Long> getStateCountNotAttributed(String campaignId, List<String> ouIds, Long date);

    Map<String, Long> getStateCountByCampaignId(String campaignId, Long date);

    Long getTotalStateCount(String campaignId, String interviewerId, List<String> ouIds, Long date);

    List<StateCount> findGroupedByCampaign(List<String> campaignIds, List<String> ouIds, Long date);

    List<StateCount> findGroupedByOu(String campaignId, List<String> ouIds, Long dateToUse);

    boolean existsById(Long id);

    StateDB save(StateDB state);
}
