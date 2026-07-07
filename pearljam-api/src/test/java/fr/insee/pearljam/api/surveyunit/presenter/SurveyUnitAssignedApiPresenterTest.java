package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
            "Idep",
            "Jean",
            "Dupont",
            "12345",
            "Paris",
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

}
