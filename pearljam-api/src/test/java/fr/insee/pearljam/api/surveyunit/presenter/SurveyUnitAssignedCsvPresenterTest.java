package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.api.surveyunit.csv.SurveyUnitAssignedCsv;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SurveyUnitAssignedCsvPresenterTest {

    private final SurveyUnitAssignedCsvPresenter presenter = new SurveyUnitAssignedCsvPresenter();

    private static void assertHeadersIsCorrect(CsvRow headersRow) {
        assertEquals(9, headersRow.values().size());
        // maybe other assertions
    }

    @Test
    void emptyContent_emptyCsv() {
        SurveyUnitAssignedCsv result = presenter.present(Page.empty());
        assertHeadersIsCorrect(result.headers());
        assertEquals(0, result.rows().size());
    }

    @Test
    void shouldThrowIfPaginatedContent() {
        Page<SurveyUnitAssigned> paginatedSurveyUnits = new PageImpl<>(
                List.of(fooSurveyUnitAssigned(), fooSurveyUnitAssigned()),
                PageRequest.of(1, 2),
                10
        );
        assertThrows(Exception.class, () -> presenter.present(paginatedSurveyUnits));
    }

    @Test
    void fooCsvRow() {
        Page<SurveyUnitAssigned> surveyUnit = new PageImpl<>(List.of(fooSurveyUnitAssigned()));
        SurveyUnitAssignedCsv result = presenter.present(surveyUnit);
        assertHeadersIsCorrect(result.headers());
        assertEquals(1, result.rows().size());
        assertEquals(
                List.of("foo-id", "FOO_LABEL", "John Doe", "Idep", "1", "33", "City", "Foo state", "-"),
                result.rows().getFirst().values());
    }

    private static @NonNull SurveyUnitAssigned fooSurveyUnitAssigned() {
        return new SurveyUnitAssigned(
                "foo-id",
                "FOO_LABEL",
                "1",
                "Idep",

                "John",
                "Doe",
                "33",
                "City",
                "Foo state",
                "-");
    }

}
