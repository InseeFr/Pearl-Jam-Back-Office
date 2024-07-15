package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.surveyunit.model.question.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IdentificationStateTest {

    @Test
    @DisplayName("Should return MISSING when identification is null")
    void testState01() {
        Identification identification1 = new Identification(
                null,
                AccessQuestionValue.NACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED);

        List<Identification> identifications = new ArrayList<>();
        identifications.add(identification1);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck)).isEqualTo(IdentificationState.MISSING));
    }

    @Test
    @DisplayName("Should return FINISHED when identification question value is destroy or unidentified")
    void testState02() {
        Identification identificationDestroy = new Identification(
                IdentificationQuestionValue.DESTROY, null, null, null, null);
        Identification identificationUnidentified = new Identification(
                IdentificationQuestionValue.UNIDENTIFIED, null, null, null, null);

        List<Identification> identifications = List.of(identificationDestroy, identificationUnidentified);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return FINISHED when situation question value is absorbed or no ordinary")
    void testState03() {
        Identification identificationAbsorbed = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ABSORBED, null, null);
        Identification identificationNoOrdinary = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.NOORDINARY, null, null);

        List<Identification> identifications = List.of(identificationAbsorbed, identificationNoOrdinary);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return FINISHED when category question value is vacant or secondary")
    void testState04() {
        Identification identificationVacant = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, null, CategoryQuestionValue.VACANT, null);
        Identification identificationSecondary = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, null, CategoryQuestionValue.SECONDARY, null);

        List<Identification> identifications = List.of(identificationVacant, identificationSecondary);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return FINISHED when occupant is not null")
    void testState05() {
        Identification identificationToCheck = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, null, null, OccupantQuestionValue.IDENTIFIED);

        assertThat(IdentificationState.getState(identificationToCheck)).isEqualTo(IdentificationState.FINISHED);
    }

    @Test
    @DisplayName("Should return ONGOING state")
    void testState06() {
        Identification identificationToCheck = new Identification(
                IdentificationQuestionValue.IDENTIFIED, null, null, null, OccupantQuestionValue.IDENTIFIED);

        assertThat(IdentificationState.getState(identificationToCheck)).isEqualTo(IdentificationState.ONGOING);
    }
}