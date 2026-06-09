package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.count.StateCount;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.StateJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StateDaoAdapter implements StateRepository {
    private final StateJpaRepository stateJpaRepository;
    private final EntityManager em;

    @Override
    public StateDto findFirstDtoBySurveyUnitOrderByDateDesc(SurveyUnitDB surveyUnit) {
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
    public StateDB save(StateDB state) {
        return stateJpaRepository.save(state);
    }

    @Override
    public Optional<StateType> findLastStateBySurveyUnitId(String surveyUnitId) {
        return stateJpaRepository.findLastStateBySurveyUnitId(surveyUnitId);
    }

    @Override
    public void saveStateForSurveyUnits(List<String> surveyUnitIds, StateType stateType, Instant date) {
        List<StateDB> states = new ArrayList<>();

        for (String id : surveyUnitIds) {
            SurveyUnitDB surveyUnit = em.getReference(SurveyUnitDB.class, id);

            StateDB state = new StateDB();
            state.setSurveyUnit(surveyUnit);
            state.setType(stateType);
            state.setDate(date.toEpochMilli());

            states.add(state);
        }

        stateJpaRepository.saveAll(states);
    }


}
