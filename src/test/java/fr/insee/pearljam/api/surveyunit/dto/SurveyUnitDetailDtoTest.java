package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.surveyunit.dto.identification.IdentificationDto;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestStatusDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.HouseF2FIdentificationDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.IdentificationDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitDetailDtoTest {
	private SurveyUnit surveyUnit;

	@BeforeEach
	void setup() {
		Address address = new InseeAddress("l1", "l2", "l3", "l4", "l5", "l6", "l7", true,
				"building", "floor", "door", "staircase", true);
		SampleIdentifier sampleIdentifier = new InseeSampleIdentifier(1, "ec", 2, 3, 4, 5, 6,
				7, 8, "autre", "nograp");
		Campaign campaign = new Campaign("id", "label", null,
				null, null, "email");
		surveyUnit = new SurveyUnit("id", true, true, address,
				sampleIdentifier, campaign, null, null, new HashSet<>());

		surveyUnit.getComments().addAll(Set.of(
				new CommentDB(1L, CommentType.INTERVIEWER, "value1", surveyUnit),
				new CommentDB(2L, CommentType.INTERVIEWER, "value2", surveyUnit),
				new CommentDB(3L, CommentType.MANAGEMENT, "value3", surveyUnit)
		));
	}

	@Test
	@DisplayName("Should have identification")
	void testCreateIdentification01() {
		IdentificationDB identificationDB = new HouseF2FIdentificationDB(1L, surveyUnit,
				IdentificationQuestionValue.IDENTIFIED,
				AccessQuestionValue.ACC,
				SituationQuestionValue.ORDINARY,
				CategoryQuestionValue.SECONDARY,
				OccupantQuestionValue.IDENTIFIED);
		surveyUnit.setIdentification(identificationDB);
		SurveyUnitDetailDto surveyUnitDetailDto = new SurveyUnitDetailDto(surveyUnit);
		IdentificationDto identificationDto = surveyUnitDetailDto.getIdentification();
		IdentificationDto identificationDtoExpected =
				IdentificationDto.fromModel(IdentificationDB.toModel(identificationDB));
		assertThat(identificationDto).isEqualTo(identificationDtoExpected);
	}

	@Test
	@DisplayName("Should have comments")
	void testCreateComments01() {
		SurveyUnitDetailDto surveyUnitDetailDto = new SurveyUnitDetailDto(surveyUnit);
		assertThat(surveyUnitDetailDto.getComments())
				.containsExactlyInAnyOrder(
						new CommentDto(CommentType.INTERVIEWER, "value1"),
						new CommentDto(CommentType.INTERVIEWER, "value2"),
						new CommentDto(CommentType.MANAGEMENT, "value3")
				);
	}

	@Test
	@DisplayName("Should have communication requests")
	void testCreateCommunicationRequests01() {

		Set<CommunicationRequestDB> communicationRequestDBs = new HashSet<>();

		List<CommunicationRequestStatusDB> status1 = List.of(
				new CommunicationRequestStatusDB(null, 1233456789L, CommunicationStatusType.INITIATED, null),
				new CommunicationRequestStatusDB(2L, 123345678910L, CommunicationStatusType.FAILED, null)
		);

		List<CommunicationRequestStatusDB> status2 = List.of(
				new CommunicationRequestStatusDB(3L, 123345678911L, CommunicationStatusType.READY, null),
				new CommunicationRequestStatusDB(4L, 123345678912L, CommunicationStatusType.CANCELLED, null)
		);

		CommunicationTemplateDB communicationTemplate1 = new CommunicationTemplateDB(1L, null, null, null, null);
		CommunicationTemplateDB communicationTemplate2 = new CommunicationTemplateDB(2L, null, null, null, null);


		communicationRequestDBs.add(new CommunicationRequestDB(10L, communicationTemplate1,
				CommunicationRequestReason.UNREACHABLE,
				CommunicationRequestEmitter.INTERVIEWER, surveyUnit, status1));
		communicationRequestDBs.add(new CommunicationRequestDB(11L, communicationTemplate2,
				CommunicationRequestReason.REFUSAL,
				CommunicationRequestEmitter.TOOL, surveyUnit, status2));
		surveyUnit.setCommunicationRequests(communicationRequestDBs);

		SurveyUnitDetailDto surveyUnitDetailDto = new SurveyUnitDetailDto(surveyUnit);

		List<CommunicationRequestStatusDto> status1Expected = List.of(
				new CommunicationRequestStatusDto(1233456789L, CommunicationStatusType.INITIATED),
				new CommunicationRequestStatusDto(123345678910L, CommunicationStatusType.FAILED)
		);

		List<CommunicationRequestStatusDto> status2Expected = List.of(
				new CommunicationRequestStatusDto(123345678911L, CommunicationStatusType.READY),
				new CommunicationRequestStatusDto(123345678912L, CommunicationStatusType.CANCELLED)
		);

		assertThat(surveyUnitDetailDto.getCommunicationRequests())
				.containsExactlyInAnyOrder(
						new CommunicationRequestResponseDto(1L,
								CommunicationRequestReason.UNREACHABLE,
								CommunicationRequestEmitter.INTERVIEWER, status1Expected),
						new CommunicationRequestResponseDto(2L,
								CommunicationRequestReason.REFUSAL,
								CommunicationRequestEmitter.TOOL, status2Expected)
				);
	}
}
