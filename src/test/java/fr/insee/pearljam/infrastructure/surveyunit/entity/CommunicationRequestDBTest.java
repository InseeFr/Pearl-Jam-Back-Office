package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.communication.*;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDBId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class CommunicationRequestDBTest {

	private final SurveyUnit surveyUnit = new SurveyUnit();
	private final CommunicationTemplateDB communicationTemplateDB = new CommunicationTemplateDB();
	private final CommunicationTemplateDBId communicationTemplateDBId = new CommunicationTemplateDBId("mesh1",
			"SIMPSONS2020X00");

	@Test
	@DisplayName("Should return model object")
	void testToModel01() {
		List<CommunicationRequestStatusDB> statusDB = List.of(
				new CommunicationRequestStatusDB(null, 1233456789L, CommunicationStatusType.INITIATED, null),
				new CommunicationRequestStatusDB(1L, 123345678910L, CommunicationStatusType.FAILED, null)
		);
		CommunicationRequestDB communicationRequestDB = new CommunicationRequestDB(1L, communicationTemplateDBId,
				CommunicationRequestReason.UNREACHABLE,
				CommunicationRequestEmitter.INTERVIEWER,
				surveyUnit,
				statusDB);

		CommunicationRequest communicationRequest = CommunicationRequestDB.toModel(communicationRequestDB);
		assertThat(communicationRequest.id()).isEqualTo(communicationRequestDB.getId());
		assertThat(communicationRequest.meshuggahId()).isEqualTo(communicationRequestDB.getCommunicationTemplateDBId().getMeshuggahId());
		assertThat(communicationRequest.campaignId()).isEqualTo(communicationRequestDB.getCommunicationTemplateDBId().getCampaignId());
		assertThat(communicationRequest.reason()).isEqualTo(communicationRequestDB.getReason());
		List<CommunicationRequestStatus> status = statusDB.stream()
				.map(CommunicationRequestStatusDB::toModel)
				.toList();
		assertThat(communicationRequest.status()).containsExactlyInAnyOrderElementsOf(status);
	}

	@Test
	@DisplayName("Should return entity object")
	void testFromModel01() {
		List<CommunicationRequestStatus> status = List.of(
				new CommunicationRequestStatus(null, 1233456789L, CommunicationStatusType.INITIATED),
				new CommunicationRequestStatus(2L, 123345678910L, CommunicationStatusType.FAILED)
		);

		CommunicationRequest communicationRequest = new CommunicationRequest(1L,
				communicationTemplateDBId.getCampaignId(), communicationTemplateDBId.getMeshuggahId(),
				CommunicationRequestReason.UNREACHABLE,
				CommunicationRequestEmitter.INTERVIEWER,
				status);

		communicationTemplateDB.setCommunicationTemplateDBId(communicationTemplateDBId);

		CommunicationRequestDB communicationRequestDB = CommunicationRequestDB.fromModel(communicationRequest,
				surveyUnit, communicationTemplateDB);
		assertThat(communicationRequestDB.getId()).isEqualTo(communicationRequest.id());
		assertThat(communicationRequestDB.getCommunicationTemplateDBId().getCampaignId()).isEqualTo(communicationRequest.campaignId());
		assertThat(communicationRequestDB.getCommunicationTemplateDBId().getMeshuggahId()).isEqualTo(communicationRequest.meshuggahId());
		assertThat(communicationRequestDB.getReason()).isEqualTo(communicationRequest.reason());
		assertThat(communicationRequestDB.getStatus())
				.extracting(CommunicationRequestStatusDB::getId,
						CommunicationRequestStatusDB::getDate,
						CommunicationRequestStatusDB::getStatus,
						CommunicationRequestStatusDB::getCommunicationRequest)
				.containsExactlyInAnyOrder(
						tuple(null, 1233456789L, CommunicationStatusType.INITIATED, communicationRequestDB),
						tuple(2L, 123345678910L, CommunicationStatusType.FAILED, communicationRequestDB)
				);
	}
}
