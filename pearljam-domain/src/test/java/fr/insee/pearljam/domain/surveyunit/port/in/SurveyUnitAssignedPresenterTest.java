package fr.insee.pearljam.domain.surveyunit.port.in;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitAssignedPresenterTest {

    @ParameterizedTest
    @CsvSource(value = {
            "Jean,Dupont,Jean Dupont",
            "Marie,Martin,Marie Martin",
            "Élodie,Dubois,Élodie Dubois",
            "Jean-Pierre,Durand,Jean-Pierre Durand",
            "Jean Claude,Martin,Jean Claude Martin",
            "Louis de,Fontaine,Louis de Fontaine",
            ",Durand,Durand",
            "Marie,,Marie",
            ",, ''"
    })
    void buildInterviewerLabel_shouldHandleFrenchNames(String firstName, String lastName, String expected) {
        assertThat(SurveyUnitAssignedPresenter.buildInterviewerLabel(firstName, lastName))
                .isEqualTo(expected);
    }

}
