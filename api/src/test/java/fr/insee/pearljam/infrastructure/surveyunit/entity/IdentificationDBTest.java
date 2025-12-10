package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.HouseF2FIdentificationDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.IdentificationDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdentificationDBTest {

	private IdentificationDB identificationDB;
	private SurveyUnit surveyUnitDB;

	private final Long identificationId = 1L;

	@BeforeEach
	void setup() {
		surveyUnitDB = new SurveyUnit();
		surveyUnitDB.setId("1");
		identificationDB = new HouseF2FIdentificationDB(identificationId,
				surveyUnitDB,
				IdentificationQuestionValue.IDENTIFIED,
				AccessQuestionValue.ACC,
				SituationQuestionValue.ABSORBED,
				CategoryQuestionValue.SECONDARY,
				OccupantQuestionValue.IDENTIFIED
		);
	}

	@Test
	@DisplayName("Should update the db entity")
	void testUpdate01() {
		Identification identification = new Identification(
				identificationId,
				IdentificationType.HOUSEF2F,
				IdentificationQuestionValue.UNIDENTIFIED,
				AccessQuestionValue.NACC,
				SituationQuestionValue.NOORDINARY,
				CategoryQuestionValue.VACANT,
				OccupantQuestionValue.UNIDENTIFIED,
				null, null, null, null, null);
		identificationDB.update(identification);
		HouseF2FIdentificationDB houseF2FIdentificationDB = (HouseF2FIdentificationDB) identificationDB;
		assertThat(houseF2FIdentificationDB.getId()).isEqualTo(identificationId);
		assertThat(houseF2FIdentificationDB.getIdentification()).isEqualTo(identification.identification());
		assertThat(houseF2FIdentificationDB.getOccupant()).isEqualTo(identification.occupant());
		assertThat(houseF2FIdentificationDB.getCategory()).isEqualTo(identification.category());
		assertThat(houseF2FIdentificationDB.getAccess()).isEqualTo(identification.access());
		assertThat(houseF2FIdentificationDB.getSituation()).isEqualTo(identification.situation());
		assertThat(identificationDB.getSurveyUnit()).isEqualTo(surveyUnitDB);
	}

	@Test
	@DisplayName("Should not update db entity when identification is null")
	void testUpdate02() {
		identificationDB.update(null);
		HouseF2FIdentificationDB houseF2FIdentificationDB = (HouseF2FIdentificationDB) identificationDB;
		assertThat(houseF2FIdentificationDB.getId()).isEqualTo(identificationId);
		assertThat(houseF2FIdentificationDB.getIdentification()).isEqualTo(IdentificationQuestionValue.IDENTIFIED);
		assertThat(houseF2FIdentificationDB.getOccupant()).isEqualTo(OccupantQuestionValue.IDENTIFIED);
		assertThat(houseF2FIdentificationDB.getCategory()).isEqualTo(CategoryQuestionValue.SECONDARY);
		assertThat(houseF2FIdentificationDB.getAccess()).isEqualTo(AccessQuestionValue.ACC);
		assertThat(houseF2FIdentificationDB.getSituation()).isEqualTo(SituationQuestionValue.ABSORBED);
		assertThat(houseF2FIdentificationDB.getSurveyUnit()).isEqualTo(surveyUnitDB);
	}

	@Test
	@DisplayName("Should create model")
	void testToModel01() {
		Identification identification = IdentificationDB.toModel(identificationDB);
		HouseF2FIdentificationDB houseF2FIdentificationDB = (HouseF2FIdentificationDB) identificationDB;
		assertThat(houseF2FIdentificationDB.getIdentification()).isEqualTo(identification.identification());
		assertThat(houseF2FIdentificationDB.getOccupant()).isEqualTo(identification.occupant());
		assertThat(houseF2FIdentificationDB.getCategory()).isEqualTo(identification.category());
		assertThat(houseF2FIdentificationDB.getAccess()).isEqualTo(identification.access());
		assertThat(houseF2FIdentificationDB.getSituation()).isEqualTo(identification.situation());
	}

	@Test
	@DisplayName("Should return null when identification db is null")
	void testToModel02() {
		assertThat(IdentificationDB.toModel(null)).isNull();
	}
}
