package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitAssignedApiPresenterTest {

    SurveyUnitAssignedApiPresenter presenter = new SurveyUnitAssignedApiPresenter();


    @Test
    void present_shouldMapPageCorrectly() {
        SurveyUnitAssigned input = new SurveyUnitAssigned(
            "ID1",
            "UNIT-1",
            "22",
            "Jean",
            "Dupont",
            "12345 Paris",
            "NNS",
            "NPX"
        );

        Page<SurveyUnitAssigned> page = new PageImpl<>(
            List.of(input),
            PageRequest.of(1, 10),
            25
        );

        var result = presenter.present(page);

        assertThat(result.content()).hasSize(1);

        var dto = result.content().getFirst();

        assertThat(dto.surveyUnitId()).isEqualTo("ID1");
        assertThat(dto.surveyUnitDisplayName()).isEqualTo("UNIT-1");
        assertThat(dto.interviewerLabel()).isEqualTo("Jean Dupont");
        assertThat(dto.ssech()).isEqualTo("22");

        assertThat(dto.location()).isEqualTo("12345");
        assertThat(dto.city()).isEqualTo("Paris");

        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(25);
        assertThat(result.totalPages()).isEqualTo(3);
    }

    @ParameterizedTest
    @CsvSource({
        "75001 Paris,75001",
        "69001 Lyon,69001",
        "13100 Aix-en-Provence,13100",
        "85100 Les Sables d'Olonne,85100",
        "42000 Saint-Étienne,42000"
    })
    void buildLocation_shouldExtractPostalCode(String addressL6, String expectedPostalCode) {
        assertThat(presenter.buildLocation(addressL6))
            .isEqualTo(expectedPostalCode);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void buildLocation_shouldReturnNullWhenAddressIsNullOrEmpty(String addressL6) {
        assertThat(presenter.buildLocation(addressL6))
            .isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "75001 Paris,Paris",
        "69001 Lyon,Lyon",
        "13100 Aix-en-Provence,Aix-en-Provence",
        "'85100 Les Sables d''Olonne','Les Sables d''Olonne'",
        "42000 Saint-Étienne,Saint-Étienne"
    })
    void buildCity_shouldExtractCity(String addressL6, String expectedCity) {
        assertThat(presenter.buildCity(addressL6))
            .isEqualTo(expectedCity);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void buildCity_shouldReturnNullWhenAddressIsNullOrEmpty(String addressL6) {
        assertThat(presenter.buildCity(addressL6))
            .isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "75001",
        "69001",
        "13100"
    })
    void buildCity_shouldReturnNullWhenCityPartIsMissing(String addressL6) {
        assertThat(presenter.buildCity(addressL6))
            .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "VIC",
        "PRC",
        "AOC"
    })
    void toStateType_shouldReturnEnum_whenValueIsValid(String value) {
        assertThat(presenter.toStateType(value))
            .isEqualTo(StateType.valueOf(value));
    }


    @ParameterizedTest
    @NullSource
    void toStateType_shouldReturnNull_whenValueIsNull(String value) {
        assertThat(presenter.toStateType(value))
            .isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "draft",
        "UNKNOWN",
        "invalid"
    })
    void toStateType_shouldThrowException_whenValueIsInvalid(String value) {
        assertThatThrownBy(() -> presenter.toStateType(value))
            .isInstanceOf(IllegalArgumentException.class);
    }


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
        assertThat(presenter.buildInterviewerLabel(firstName, lastName))
            .isEqualTo(expected);
    }

}