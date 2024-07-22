package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestCreateDto;
import fr.insee.pearljam.api.surveyunit.dto.IdentificationDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
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
    public void updateSurveyUnitInfos(SurveyUnit surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
        if(surveyUnitUpdateDto.comments() != null) {
            Set<Comment> commentsToUpdate = surveyUnitUpdateDto.comments().stream()
                    .map(commentDto -> CommentDto.toModel(surveyUnit.getId(), commentDto))
                    .collect(Collectors.toSet());

            surveyUnit.updateComments(commentsToUpdate);
        }
        if(surveyUnitUpdateDto.communicationRequests() != null) {
            List<CommunicationRequest> communicationRequests = CommunicationRequestCreateDto.toModel(surveyUnitUpdateDto.communicationRequests());
            surveyUnit.addCommunicationRequests(communicationRequests);
        }

        Identification identification = IdentificationDto.toModel(surveyUnitUpdateDto.identification());
        surveyUnit.updateIdentification(identification);
    }
}
