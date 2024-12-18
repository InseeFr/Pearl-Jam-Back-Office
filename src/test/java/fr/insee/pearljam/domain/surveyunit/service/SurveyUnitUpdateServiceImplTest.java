package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.campaign.controller.dummy.VisibilityFakeService;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.OrganizationUnitType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.service.impl.SurveyUnitUpdateServiceImpl;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestCreateDto;
import fr.insee.pearljam.api.surveyunit.dto.IdentificationDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.campaign.port.userside.DateService;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import fr.insee.pearljam.domain.surveyunit.service.dummy.CommunicationRequestFakeRepository;
import fr.insee.pearljam.domain.surveyunit.service.dummy.CommunicationTemplateFakeRepository;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.IdentificationDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class SurveyUnitUpdateServiceImplTest {
    private CommunicationRequestFakeRepository communicationRequestFakeRepository;
    private VisibilityFakeService visibilityFakeService;
    private SurveyUnitUpdateServiceImpl surveyUnitService;
    private SurveyUnit surveyUnit;
    private SurveyUnitUpdateDto surveyUnitDto;
    private DateService dateService;
    private CommunicationTemplateDB communicationTemplateDB;
    private CommunicationTemplate communicationTemplate;
    private CommunicationTemplateFakeRepository communicationTemplateFakeRepository;
    private Campaign campaign;
    private OrganizationUnit ou;

    @BeforeEach
    void setup() {
        visibilityFakeService = new VisibilityFakeService();
        communicationTemplateFakeRepository = new CommunicationTemplateFakeRepository();
        dateService = new FixedDateService();
        communicationRequestFakeRepository = new CommunicationRequestFakeRepository();
        surveyUnitService = new SurveyUnitUpdateServiceImpl(communicationRequestFakeRepository, communicationTemplateFakeRepository, visibilityFakeService, dateService);
        campaign = new Campaign("campaignId", "label", null, null, null,null);
        ou = new OrganizationUnit("ouId", "label-ou", OrganizationUnitType.LOCAL);
        Visibility visibility = new Visibility(campaign.getId(), ou.getId(), null, null,
                null, null, null, null, true, "mail", "tel");
        visibilityFakeService.save(visibility);
        surveyUnit = new SurveyUnit("id", true, true, null,
                null, campaign, null, ou, null);

        communicationTemplate = new CommunicationTemplate(3L, "messhId", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        communicationTemplateFakeRepository.save(communicationTemplate);
        communicationTemplateDB = new CommunicationTemplateDB(3L, "messhId", CommunicationMedium.EMAIL, CommunicationType.NOTICE, campaign);
        Set<CommunicationRequestDB> communicationRequestDBs = new HashSet<>();
        communicationRequestDBs.add(new CommunicationRequestDB(10L, communicationTemplateDB,
                CommunicationRequestReason.REFUSAL,
                CommunicationRequestEmitter.TOOL,
                surveyUnit, null));
        surveyUnit.setCommunicationRequests(communicationRequestDBs);
    }

    @Test
    @DisplayName("Should add communication requests for survey unit")
    void testUpdateCommunication01() {
        List<CommunicationRequestCreateDto> communicationRequests = List.of(
                new CommunicationRequestCreateDto(communicationTemplate.id(), 12345678910L,
                        CommunicationRequestReason.UNREACHABLE),
                new CommunicationRequestCreateDto(communicationTemplate.id(), 1234567891011L,
                        CommunicationRequestReason.REFUSAL)
        );

        surveyUnitDto = createSurveyUnitDto(null, null, communicationRequests);
        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);

        List<CommunicationRequest> communicationRequestResults = communicationRequestFakeRepository.getCommunicationRequestsAdded();
        assertThat(communicationRequestResults)
                .hasSize(2)
                .extracting(CommunicationRequest::id,
                        CommunicationRequest::communicationTemplateId,
                        CommunicationRequest::reason,
                        CommunicationRequest::emitter,
                        communicationRequest -> communicationRequest.status().stream()
                                .map(status -> tuple(status.id(), status.date(), status.status()))
                                .toList()
                        )
                .containsExactlyInAnyOrder(
                        tuple(null,
                                communicationTemplate.id(),
                                CommunicationRequestReason.UNREACHABLE,
                                CommunicationRequestEmitter.INTERVIEWER,
                                List.of(
                                        tuple(null, 12345678910L, CommunicationStatusType.INITIATED),
                                        tuple(null, dateService.getCurrentTimestamp(), CommunicationStatusType.READY)
                                )
                        ),
                        tuple(null,
                                communicationTemplate.id(),
                                CommunicationRequestReason.REFUSAL,
                                CommunicationRequestEmitter.INTERVIEWER,
                                List.of(
                                        tuple(null, 1234567891011L, CommunicationStatusType.INITIATED),
                                        tuple(null, dateService.getCurrentTimestamp(), CommunicationStatusType.READY)
                                )
                        )
                );
    }

    @Test
    @DisplayName("Should add CANCELLED letter communication request if visibility doesn't allow letter communications")
    void testUpdateCommunication02() {
        visibilityFakeService.clearVisibilities();
        communicationTemplateFakeRepository.clearCommunicationTemplates();
        Visibility visibility = new Visibility(campaign.getId(), ou.getId(), null, null,
                null, null, null, null, false, "mail", "tel");
        communicationTemplate = new CommunicationTemplate(3L, "messhId", CommunicationMedium.LETTER, CommunicationType.NOTICE);
        communicationTemplateFakeRepository.save(communicationTemplate);
        visibilityFakeService.save(visibility);
        List<CommunicationRequestCreateDto> communicationRequests = List.of(
                new CommunicationRequestCreateDto(communicationTemplate.id(), 12345678910L,
                        CommunicationRequestReason.UNREACHABLE),
                new CommunicationRequestCreateDto(communicationTemplate.id(), 1234567891011L,
                        CommunicationRequestReason.REFUSAL)
        );

        surveyUnitDto = createSurveyUnitDto(null, null, communicationRequests);
        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);

        List<CommunicationRequest> communicationRequestResults = communicationRequestFakeRepository.getCommunicationRequestsAdded();
        assertThat(communicationRequestResults)
                .hasSize(2)
                .extracting(CommunicationRequest::id,
                        CommunicationRequest::communicationTemplateId,
                        CommunicationRequest::reason,
                        CommunicationRequest::emitter,
                        communicationRequest -> communicationRequest.status().stream()
                                .map(status -> tuple(status.id(), status.date(), status.status()))
                                .toList()
                )
                .containsExactlyInAnyOrder(
                        tuple(null,
                                3L,
                                CommunicationRequestReason.UNREACHABLE,
                                CommunicationRequestEmitter.INTERVIEWER,
                                List.of(
                                        tuple(null, 12345678910L, CommunicationStatusType.INITIATED),
                                        tuple(null, dateService.getCurrentTimestamp(), CommunicationStatusType.CANCELLED))
                        ),
                        tuple(null,
                                3L,
                                CommunicationRequestReason.REFUSAL,
                                CommunicationRequestEmitter.INTERVIEWER,
                                List.of(
                                        tuple(null, 1234567891011L, CommunicationStatusType.INITIATED),
                                        tuple(null, dateService.getCurrentTimestamp(), CommunicationStatusType.CANCELLED))
                        )
                );
    }

    @Test
    @DisplayName("Should add comments for survey unit")
    void testUpdateComments01() {
        List<CommentDto> comments = List.of(
                new CommentDto(CommentType.INTERVIEWER, "value1"),
                new CommentDto(CommentType.MANAGEMENT, "value2")
        );

        surveyUnitDto = createSurveyUnitDto(null, comments, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        assertThat(surveyUnit.getComments())
                .hasSize(2)
                .extracting(CommentDB::getId,
                        CommentDB::getType,
                        CommentDB::getValue,
                        CommentDB::getSurveyUnit
                ).contains(
                        tuple(null, CommentType.INTERVIEWER, "value1", surveyUnit),
                        tuple(null, CommentType.MANAGEMENT, "value2", surveyUnit)
                );
    }

    @Test
    @DisplayName("Should remove survey unit comments with same type of inputted comments")
    void testUpdateComments02() {
        surveyUnit.getComments().addAll(Set.of(
                new CommentDB(1L, CommentType.INTERVIEWER, "value1", surveyUnit),
                new CommentDB(2L, CommentType.INTERVIEWER, "value2", surveyUnit),
                new CommentDB(3L, CommentType.MANAGEMENT, "value3", surveyUnit)
        ));

        List<CommentDto> comments = List.of(
                new CommentDto(CommentType.INTERVIEWER, "value4")
        );

        surveyUnitDto = createSurveyUnitDto(null, comments, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        assertThat(surveyUnit.getComments())
                .hasSize(2)
                .extracting(CommentDB::getId,
                        CommentDB::getType,
                        CommentDB::getValue,
                        CommentDB::getSurveyUnit
                ).contains(
                        tuple(null, CommentType.INTERVIEWER, "value4", surveyUnit),
                        tuple(3L, CommentType.MANAGEMENT, "value3", surveyUnit)
                );
    }

    @Test
    @DisplayName("Should update identification")
    void testUpdateIdentification01() {
        IdentificationDB identificationDB = createIdentificationDB();
        surveyUnit.setIdentification(identificationDB);

        IdentificationDto identification = new IdentificationDto(IdentificationQuestionValue.UNIDENTIFIED,
                AccessQuestionValue.NACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED,
                IndividualStatusQuestionValue.OTHERADDRESS,
                InterviewerCanProcessQuestionValue.NO,
                NumberOfRespondentsQuestionValue.MANY,
                PresentInPreviousHomeQuestionValue.NONE,
                HouseholdCompositionQuestionValue.OTHERCOMPO
        );
        surveyUnitDto = createSurveyUnitDto(identification, null, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        IdentificationDB identificationResult = surveyUnit.getIdentification();
        assertThat(identificationResult.getId()).isEqualTo(2L);
        assertThat(identificationResult.getIdentification()).isEqualTo(IdentificationQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getAccess()).isEqualTo(AccessQuestionValue.NACC);
        assertThat(identificationResult.getSituation()).isEqualTo(SituationQuestionValue.NOORDINARY);
        assertThat(identificationResult.getCategory()).isEqualTo(CategoryQuestionValue.VACANT);
        assertThat(identificationResult.getOccupant()).isEqualTo(OccupantQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getIndividualStatus()).isEqualTo(IndividualStatusQuestionValue.OTHERADDRESS);
        assertThat(identificationResult.getInterviewerCanProcess()).isEqualTo(InterviewerCanProcessQuestionValue.NO);
        assertThat(identificationResult.getNumberOfRespondents()).isEqualTo(NumberOfRespondentsQuestionValue.MANY);
        assertThat(identificationResult.getPresentInPreviousHome()).isEqualTo(PresentInPreviousHomeQuestionValue.NONE);
        assertThat(identificationResult.getHouseholdComposition()).isEqualTo(HouseholdCompositionQuestionValue.OTHERCOMPO);
        assertThat(identificationResult.getSurveyUnit()).isEqualTo(surveyUnit);
    }

    @Test
    @DisplayName("Should not update identification entity when identification model is null")
    void testUpdateIdentification02() {
        IdentificationDB identificationDB = createIdentificationDB();
        surveyUnit.setIdentification(identificationDB);
        surveyUnitDto = createSurveyUnitDto(null, null, null);
        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        assertThat(surveyUnit.getIdentification()).isEqualTo(identificationDB);
    }

    @Test
    @DisplayName("Should create identification entity when entity does not exist")
    void testUpdateIdentification03() {
        surveyUnit.setIdentification(null);
        IdentificationDto identification = new IdentificationDto(IdentificationQuestionValue.UNIDENTIFIED,
                AccessQuestionValue.NACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED,
                IndividualStatusQuestionValue.OTHERADDRESS,
                InterviewerCanProcessQuestionValue.NO,
                NumberOfRespondentsQuestionValue.MANY,
                PresentInPreviousHomeQuestionValue.NONE,
                HouseholdCompositionQuestionValue.OTHERCOMPO);
        surveyUnitDto = createSurveyUnitDto(identification, null, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);

        IdentificationDB identificationResult = surveyUnit.getIdentification();
        assertThat(identificationResult.getId()).isNull();
        assertThat(identificationResult.getIdentification()).isEqualTo(IdentificationQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getAccess()).isEqualTo(AccessQuestionValue.NACC);
        assertThat(identificationResult.getSituation()).isEqualTo(SituationQuestionValue.NOORDINARY);
        assertThat(identificationResult.getCategory()).isEqualTo(CategoryQuestionValue.VACANT);
        assertThat(identificationResult.getOccupant()).isEqualTo(OccupantQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(identificationResult.getIndividualStatus()).isEqualTo(IndividualStatusQuestionValue.OTHERADDRESS);
        assertThat(identificationResult.getInterviewerCanProcess()).isEqualTo(InterviewerCanProcessQuestionValue.NO);
        assertThat(identificationResult.getNumberOfRespondents()).isEqualTo(NumberOfRespondentsQuestionValue.MANY);
        assertThat(identificationResult.getPresentInPreviousHome()).isEqualTo(PresentInPreviousHomeQuestionValue.NONE);
        assertThat(identificationResult.getHouseholdComposition()).isEqualTo(HouseholdCompositionQuestionValue.OTHERCOMPO);
    }

    private SurveyUnitUpdateDto createSurveyUnitDto(IdentificationDto identification, List<CommentDto> comments, List<CommunicationRequestCreateDto> communicationRequests) {
        return new SurveyUnitUpdateDto("su-id", null, null, true,
                comments, null, null, null, identification, communicationRequests);
    }

    private IdentificationDB createIdentificationDB() {
        return new IdentificationDB(2L,
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.SAMEADDRESS,
                InterviewerCanProcessQuestionValue.YES,
                NumberOfRespondentsQuestionValue.ONE,
                PresentInPreviousHomeQuestionValue.ATLEASTONE,
                HouseholdCompositionQuestionValue.SAMECOMPO,
                surveyUnit);
    }
}
