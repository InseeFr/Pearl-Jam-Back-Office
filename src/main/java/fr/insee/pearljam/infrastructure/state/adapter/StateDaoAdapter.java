package fr.insee.pearljam.infrastructure.state.adapter;

import fr.insee.pearljam.api.state.dto.StateDto;
import fr.insee.pearljam.domain.count.model.StateCount;
import fr.insee.pearljam.domain.state.model.State;
import fr.insee.pearljam.domain.state.port.serverside.StateRepository;
import fr.insee.pearljam.domain.surveyunit.model.SurveyUnit;
import fr.insee.pearljam.infrastructure.state.jpa.StateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StateDaoAdapter implements StateRepository {
    private final StateJpaRepository stateJpaRepository;

    @Override
    public StateDto findFirstDtoBySurveyUnitOrderByDateDesc(SurveyUnit surveyUnit) {
        return stateJpaRepository.findFirstDtoBySurveyUnitOrderByDateDesc(surveyUnit);
    }

    @Override
    public List<StateDto> findAllDtoBySurveyUnitIdOrderByDateAsc(String suId) {
        return stateJpaRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(suId);
    }

    @Override
    public StateDto findFirstDtoBySurveyUnitIdOrderByDateDesc(String surveyUnitId) {
        return stateJpaRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(surveyUnitId);
    }

    @Override
    public Map<String, Long> getStateCount(String campaignId, String interviewerId, List<String> ouIds, Long date) {
        return stateJpaRepository.getStateCount(campaignId, interviewerId, ouIds, date);
    }

    @Override
    public Map<String, Long> getStateCountSumByInterviewer(List<String> campaignIds, String interviewerId, List<String> ouIds, Long date) {
        return stateJpaRepository.getStateCountSumByInterviewer(campaignIds, interviewerId, ouIds, date);
    }

    @Override
    public Map<String, Long> getStateCountNotAttributed(String campaignId, List<String> ouIds, Long date) {
        return stateJpaRepository.getStateCountNotAttributed(campaignId, ouIds, date);
    }

    @Override
    public Map<String, Long> getStateCountByCampaignId(String campaignId, Long date) {
        return stateJpaRepository.getStateCountByCampaignId(campaignId, date);
    }

    @Override
    public Long getTotalStateCount(String campaignId, String interviewerId, List<String> ouIds, Long date) {
        return stateJpaRepository.getTotalStateCount(campaignId, interviewerId, ouIds, date);
    }

    @Override
    public List<StateCount> findGroupedByCampaign(List<String> campaignIds, List<String> ouIds, Long date) {
        return stateJpaRepository.findGroupedByCampaign(campaignIds, ouIds, date);
    }

    @Override
    public List<StateCount> findGroupedByOu(String campaignId, List<String> ouIds, Long dateToUse) {
        return stateJpaRepository.findGroupedByOu(campaignId, ouIds, dateToUse);
    }

    @Override
    public boolean existsById(Long id) {
        return stateJpaRepository.existsById(id);
    }

    @Override
    public State save(State state) {
        return stateJpaRepository.save(state);
    }
}
