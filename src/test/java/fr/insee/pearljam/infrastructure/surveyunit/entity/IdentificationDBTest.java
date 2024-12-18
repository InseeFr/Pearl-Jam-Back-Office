package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
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
        identificationDB = new IdentificationDB(identificationId,
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ABSORBED,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.IDENTIFIED, null, null, null, null, null,
                surveyUnitDB);
    }

    @Test
    @DisplayName("Should update the db entity")
    void testUpdate01() {
        Identification identification = new Identification(IdentificationQuestionValue.UNIDENTIFIED,
                AccessQuestionValue.NACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED, null, null, null, null, null);
        identificationDB.update(identification);
        assertThat(identificationDB.getId()).isEqualTo(identificationId);
        assertThat(identificationDB.getIdentification()).isEqualTo(identification.identification());
        assertThat(identificationDB.getOccupant()).isEqualTo(identification.occupant());
        assertThat(identificationDB.getCategory()).isEqualTo(identification.category());
        assertThat(identificationDB.getAccess()).isEqualTo(identification.access());
        assertThat(identificationDB.getSituation()).isEqualTo(identification.situation());
        assertThat(identificationDB.getSurveyUnit()).isEqualTo(surveyUnitDB);
    }

    @Test
    @DisplayName("Should not update db entity when identification is null")
    void testUpdate02() {
        identificationDB.update(null);
        assertThat(identificationDB.getId()).isEqualTo(identificationId);
        assertThat(identificationDB.getIdentification()).isEqualTo(IdentificationQuestionValue.IDENTIFIED);
        assertThat(identificationDB.getOccupant()).isEqualTo(OccupantQuestionValue.IDENTIFIED);
        assertThat(identificationDB.getCategory()).isEqualTo(CategoryQuestionValue.SECONDARY);
        assertThat(identificationDB.getAccess()).isEqualTo(AccessQuestionValue.ACC);
        assertThat(identificationDB.getSituation()).isEqualTo(SituationQuestionValue.ABSORBED);
        assertThat(identificationDB.getSurveyUnit()).isEqualTo(surveyUnitDB);
    }

    @Test
    @DisplayName("Should create model")
    void testToModel01() {
        Identification identification = IdentificationDB.toModel(identificationDB);
        assertThat(identificationDB.getIdentification()).isEqualTo(identification.identification());
        assertThat(identificationDB.getOccupant()).isEqualTo(identification.occupant());
        assertThat(identificationDB.getCategory()).isEqualTo(identification.category());
        assertThat(identificationDB.getAccess()).isEqualTo(identification.access());
        assertThat(identificationDB.getSituation()).isEqualTo(identification.situation());
    }

    @Test
    @DisplayName("Should return null when identification db is null")
    void testToModel02() {
        assertThat(IdentificationDB.toModel(null)).isNull();
    }
}
