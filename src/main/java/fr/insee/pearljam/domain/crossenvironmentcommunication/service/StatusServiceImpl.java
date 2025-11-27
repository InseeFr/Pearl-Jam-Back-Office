package fr.insee.pearljam.domain.crossenvironmentcommunication.service;

import fr.insee.pearljam.domain.crossenvironmentcommunication.model.SurveyUnitStatus;
import fr.insee.pearljam.domain.crossenvironmentcommunication.port.serverside.SurveyUnitStatusRepository;
import fr.insee.pearljam.domain.crossenvironmentcommunication.port.userside.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final SurveyUnitStatusRepository repository;

    @Override
    public void updateStatus(String surveyUnitId, String type) {
        var status = new SurveyUnitStatus(surveyUnitId, type);
        this.repository.save(status);
    }
}