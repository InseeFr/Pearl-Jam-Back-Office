package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitHistoryRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCommunication;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitState;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyUnitHistoryDaoAdapter implements SurveyUnitHistoryRepositoryPort {
    private final SurveyUnitRepository surveyUnitRepository;
    private final StateRepository stateRepository;
    private final CommunicationRequestRepository communicationRepository;


    @Override
    public SurveyUnitHistory findSurveyUnitHistory(String surveyUnitId) {
        SurveyUnitDB surveyUnitDB = surveyUnitRepository.findById(surveyUnitId)
                .orElseThrow();

        List<SurveyUnitState> states =
                stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(surveyUnitId)
                        .stream()
                        .map(state -> new SurveyUnitState(
                                state.date(),
                                state.type()))
                        .toList();

        List<SurveyUnitCommunication> communications =
                communicationRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(surveyUnitId)
                        .stream()
                        .map(comm -> new SurveyUnitCommunication(
                                comm.date(),
                                CommunicationType.fromCode(comm.type())
                        ))
                        .toList();

        return new SurveyUnitHistory(
                surveyUnitDB.getId(),
                surveyUnitDB.getDisplayName(),
                states,
                communications
        );
    }
}
