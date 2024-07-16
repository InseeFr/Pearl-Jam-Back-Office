package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestDto;
import fr.insee.pearljam.api.surveyunit.dto.IdentificationDto;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.api.service.SurveyUnitUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitUpdateServiceImpl implements SurveyUnitUpdateService {

    @Transactional
    @Override
    public void updateSurveyUnitInfos(SurveyUnit surveyUnit, SurveyUnitDetailDto surveyUnitDetailDto) {
        if(surveyUnitDetailDto.getComments() != null) {
            Set<Comment> commentsToUpdate = surveyUnitDetailDto.getComments().stream()
                    .map(commentDto -> CommentDto.toModel(surveyUnit.getId(), commentDto))
                    .collect(Collectors.toSet());

            surveyUnit.updateComments(commentsToUpdate);
        }
        if(surveyUnitDetailDto.getCommunicationRequests() != null) {
            List<CommunicationRequest> communicationRequests = surveyUnitDetailDto.getCommunicationRequests().stream()
                    .map(CommunicationRequestDto::toModel)
                    .toList();
            surveyUnit.updateCommunicationRequests(communicationRequests);
        }

        Identification identification = IdentificationDto.toModel(surveyUnitDetailDto.getIdentification());
        surveyUnit.updateIdentification(identification);
    }
}
