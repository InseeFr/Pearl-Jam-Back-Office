package fr.insee.pearljam.domain.surveyunit.stub;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.count.StateCount;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;

import java.time.Instant;
import java.util.*;

public class StateRepositoryStub implements StateRepository {

    private final Map<String, StateType> surveyUnitStates = new HashMap<>();
    private final Map<String, List<StateType>> saveHistory = new HashMap<>();

    @Override
    public StateDto findFirstDtoBySurveyUnitOrderByDateDesc(SurveyUnitDB surveyUnit) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public List<StateDto> findAllDtoBySurveyUnitIdOrderByDateAsc(String suId) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public StateDto findFirstDtoBySurveyUnitIdOrderByDateDesc(String surveyUnitId) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public Map<String, Long> getStateCount(String campaignId, String interviewerId, List<String> ouIds, Long date) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public Map<String, Long> getStateCountSumByInterviewer(List<String> campaignIds, String interviewerId, List<String> ouIds, Long date) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public Map<String, Long> getStateCountNotAttributed(String campaignId, List<String> ouIds, Long date) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public Map<String, Long> getStateCountByCampaignId(String campaignId, Long date) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public Long getTotalStateCount(String campaignId, String interviewerId, List<String> ouIds, Long date) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public List<StateCount> findGroupedByCampaign(List<String> campaignIds, List<String> ouIds, Long date) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public List<StateCount> findGroupedByOu(String campaignId, List<String> ouIds, Long dateToUse) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public boolean existsById(Long id) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public StateDB save(StateDB state) {
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public Optional<StateType> findLastStateBySurveyUnitId(String surveyUnitId) {
        return Optional.ofNullable(surveyUnitStates.get(surveyUnitId));
    }

    @Override
    public void saveStateForSurveyUnits(List<String> surveyUnitIds, StateType stateType, Instant date) {
        surveyUnitIds.forEach( id -> surveyUnitStates.put(id, stateType));
        surveyUnitIds.forEach(id -> saveHistory.computeIfAbsent(id, k -> new ArrayList<>()).add(stateType));
    }

    // Helper methods for tests
    public void setStateForSurveyUnit(String surveyUnitId, StateType stateType) {
        surveyUnitStates.put(surveyUnitId, stateType);
    }

    public List<StateType> getSavedStatesForSurveyUnit(String surveyUnitId) {
        return saveHistory.getOrDefault(surveyUnitId, Collections.emptyList());
    }

    public void reset() {
        surveyUnitStates.clear();
        saveHistory.clear();
    }
}
