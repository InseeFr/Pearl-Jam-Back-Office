package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.surveyunit.dto.person.PersonDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.CommentDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.CommunicationRequestCreateDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactOutcomeDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.campaign.model.*;
import fr.insee.pearljam.contracts.surveyunit.dto.contacthistory.NextContactHistoryDto;
import fr.insee.pearljam.contracts.surveyunit.dto.identification.IdentificationDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.service.model.Visibility;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.out.CommunicationTemplateRepository;
import fr.insee.pearljam.domain.campaign.port.in.VisibilityService;
import fr.insee.pearljam.domain.campaign.service.exception.CommunicationTemplateNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.VisibilityNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcome;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.contacthistory.Person;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitUpdateService;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.identification.IdentificationDB;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void updateSurveyUnitInfos(SurveyUnitDB surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto) {
        long timestamp = dateService.getCurrentTimestamp();
        if(surveyUnitUpdateDto.comments() != null) {
            Set<Comment> commentsToUpdate = surveyUnitUpdateDto.comments().stream()
                    .map(commentDto -> CommentDto.toModel(surveyUnit.getId(), commentDto))
                    .collect(Collectors.toSet());

            surveyUnit.updateComments(commentsToUpdate);
        }
        if(surveyUnitUpdateDto.communicationRequests() != null) {
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

        Set<Person> personsToUpdate = Optional.ofNullable(surveyUnitUpdateDto.persons()).orElse(Collections.emptyList())
                .stream().map(person -> PersonDto.toModel(person, null))
                .collect(Collectors.toSet());
        surveyUnit.updatePersons(personsToUpdate);

        //update ContactOutcome
        ContactOutcome contactOutcome = ContactOutcomeDto.toModel(surveyUnit.getId(),
            surveyUnitUpdateDto.contactOutcome());
        contactOutcome = convertDeprecatedContactOutcomeValue(contactOutcome);
        surveyUnit.updateContactOutcome(contactOutcome);

        //update ContactHistory
        Optional.ofNullable(surveyUnitUpdateDto.nextContactHistory())
                .map(NextContactHistoryDto::toModel)
                .ifPresent(surveyUnit::updateNextContactHistory);
        surveyUnit.setLastUpdated(timestamp);
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
    private CommunicationRequest getNewCommunicationRequest(CommunicationRequestCreateDto communicationRequestToCreate, SurveyUnitDB surveyUnit, Long readyTimestamp) {
        String campaignId = surveyUnit.getCampaign().getId();
        CommunicationTemplate communicationTemplate = communicationTemplateRepository
                .findCommunicationTemplate(campaignId, communicationRequestToCreate.communicationTemplateId())
                .orElseThrow(CommunicationTemplateNotFoundException::new);

        // Correcting clock desynchronization from frontend retrieved creation timestamp (for initialization status)
        // in case ready status is set before initialization status
        // by setting creation timestamp right before ready one
        long timestampDelta = communicationRequestToCreate.creationTimestamp() - readyTimestamp;
        long creationTimestamp = timestampDelta >= 0 ? readyTimestamp - 1 : communicationRequestToCreate.creationTimestamp();

        if(!communicationTemplate.medium().equals(CommunicationMedium.LETTER)) {
            return CommunicationRequest.create(
                    campaignId,
                    communicationRequestToCreate.communicationTemplateId(),
                    creationTimestamp,
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
                    creationTimestamp,
                    readyTimestamp,
                    communicationRequestToCreate.reason());
        }

        // if the communication request is a letter communication request, but the visibility doesn't admit it,
        // create a cancelled communication request
        return CommunicationRequest.createCancelled(
                campaignId,
                communicationRequestToCreate.communicationTemplateId(),
                creationTimestamp,
                dateService.getCurrentTimestamp(),
                communicationRequestToCreate.reason());
    }
}
