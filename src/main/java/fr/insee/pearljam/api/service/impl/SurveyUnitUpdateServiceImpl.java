package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestCreateDto;
import fr.insee.pearljam.api.surveyunit.dto.ContactOutcomeDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.api.surveyunit.dto.identification.IdentificationDto;
import fr.insee.pearljam.domain.campaign.port.userside.DateService;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.serverside.CommunicationTemplateRepository;
import fr.insee.pearljam.domain.campaign.port.userside.VisibilityService;
import fr.insee.pearljam.domain.exception.CommunicationTemplateNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.ContactOutcome;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.api.service.SurveyUnitUpdateService;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommunicationRequestRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.IdentificationDB;
import java.util.Optional;
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

    private final CommunicationRequestRepository communicationRequestRepository;
    private final CommunicationTemplateRepository communicationTemplateRepository;
    private final VisibilityService visibilityService;
    private final DateService dateService;

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
            Long timestamp = dateService.getCurrentTimestamp();
            List<CommunicationRequest> communicationRequestsToCreate =
                    surveyUnitUpdateDto.communicationRequests()
                            .stream()
                            .map(communicationRequestCreateDto -> getNewCommunicationRequest(communicationRequestCreateDto, surveyUnit, timestamp))
                            .toList();
            communicationRequestRepository.addCommunicationRequests(surveyUnit, communicationRequestsToCreate);
        }
        IdentificationConfiguration identificationConfiguration = surveyUnit.getCampaign().getIdentificationConfiguration();

        Identification identification = Optional.ofNullable(surveyUnitUpdateDto.identification())
            .map(idDto -> IdentificationDto.toModel(idDto, identificationConfiguration))
            .orElseGet(() -> IdentificationDB.toModel(surveyUnit.getIdentification()));

        surveyUnit.updateIdentification(identification);

        //update ContactOutcome
        ContactOutcome contactOutcome = ContactOutcomeDto.toModel(surveyUnit.getId(),
            surveyUnitUpdateDto.contactOutcome());
        contactOutcome = convertDeprecatedContactOutcomeValue(contactOutcome);
        surveyUnit.updateContactOutcome(contactOutcome);
    }

    // when DCD and DUU values are not used anymore => to be removed
    private ContactOutcome convertDeprecatedContactOutcomeValue(ContactOutcome contactOutcome) {
        if (contactOutcome == null) {
            return null;
        }
        return switch (contactOutcome.type()) {
            case DCD -> new ContactOutcome(contactOutcome.id(), contactOutcome.date(), ContactOutcomeType.NOA,
                contactOutcome.totalNumberOfContactAttempts(), contactOutcome.surveyUnitId());
            case DUU -> new ContactOutcome(contactOutcome.id(), contactOutcome.date(), ContactOutcomeType.DUK,
                contactOutcome.totalNumberOfContactAttempts(), contactOutcome.surveyUnitId());
            case INA, REF, IMP, UCD, UTR, ALA, DUK, NUH, NOA -> contactOutcome;
        };
    }

    /**
     * This method checks the validity of a communication request
     * @param communicationRequestToCreate communication request to create
     * @param surveyUnit the survey unit to update
     * @return a new communication request
     */
    private CommunicationRequest getNewCommunicationRequest(CommunicationRequestCreateDto communicationRequestToCreate, SurveyUnit surveyUnit, Long readyTimestamp) {
        String campaignId = surveyUnit.getCampaign().getId();
        CommunicationTemplate communicationTemplate = communicationTemplateRepository
                .findCommunicationTemplate(campaignId, communicationRequestToCreate.communicationTemplateId())
                .orElseThrow(CommunicationTemplateNotFoundException::new);

        if(!communicationTemplate.medium().equals(CommunicationMedium.LETTER)) {
            return CommunicationRequest.create(
                    campaignId,
                    communicationRequestToCreate.communicationTemplateId(),
                    communicationRequestToCreate.creationTimestamp(),
                    readyTimestamp,
                    communicationRequestToCreate.reason());
        }

        Visibility visibility = visibilityService
                .findVisibility(campaignId, surveyUnit.getOrganizationUnit().getId())
                .orElseThrow(VisibilityNotFoundException::new);


        if(visibility.useLetterCommunication() != null && visibility.useLetterCommunication()) {
            return CommunicationRequest.create(
                    campaignId,
                    communicationRequestToCreate.communicationTemplateId(),
                    communicationRequestToCreate.creationTimestamp(),
                    readyTimestamp,
                    communicationRequestToCreate.reason());
        }

        // if the communication request is a letter communication request, but the visibility doesn't admit it,
        // create a cancelled communication request
        return CommunicationRequest.createCancelled(
                campaignId,
                communicationRequestToCreate.communicationTemplateId(),
                communicationRequestToCreate.creationTimestamp(),
                dateService.getCurrentTimestamp(),
                communicationRequestToCreate.reason());
    }
}
