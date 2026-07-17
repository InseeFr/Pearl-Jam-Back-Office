package fr.insee.pearljam.api.surveyunit.export.completed;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitCompletedCsvPresenterTest {
    private final SurveyUnitCompletedCsvPresenter presenter = new SurveyUnitCompletedCsvPresenter();

    @Test
    void should_present_survey_units_as_csv_rows() {
        // Given
        SurveyUnitFetchedByStatesAndCampaignIdView surveyUnit = new SurveyUnitFetchedByStatesAndCampaignIdView(
                "SU001",
                "Survey Unit 1",
                null,
                "",
                "INTID",
                "1749544200000", // 2025-06-10T10:30:00Z
                "CONTACTED",
                "INA",
                true,
                "comment"
        );

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> page =
                new PageImpl<>(List.of(surveyUnit));

        // When
        SurveyUnitCompletedCsv result = presenter.present(page);

        // Then
        assertThat(result.rows()).hasSize(1);

        CsvRow row = result.rows().getFirst();

        assertThat(row.toCsvLine()).isEqualToIgnoringNewLines("""
                SU001;Survey Unit 1;;INTID;10/06/2025;CONTACTED;INA;Oui;comment
                """
        );

    }

    @Test
    void should_return_empty_string_when_viewed_is_null() {
        // Given
        SurveyUnitFetchedByStatesAndCampaignIdView surveyUnit = new SurveyUnitFetchedByStatesAndCampaignIdView(
                "SU001",
                "Survey Unit 1",
                "John",
                "Doe",
                "INTID",
                "1749544200000", // 2025-06-10T10:30:00Z
                "CONTACTED",
                "INA",
                null,
                "comment\rtest \n return carriage"
        );

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> page =
                new PageImpl<>(List.of(surveyUnit));

        // When
        SurveyUnitCompletedCsv result = presenter.present(page);

        // Then
        CsvRow row = result.rows().getFirst();

        assertThat(row.toCsvLine()).isEqualToIgnoringNewLines("""
                SU001;Survey Unit 1;John Doe;INTID;10/06/2025;CONTACTED;INA;Non;comment test   return carriage
                """);
    }

    @Test
    void should_return_empty_rows_when_page_is_empty() {
        // Given
        Page<SurveyUnitFetchedByStatesAndCampaignIdView> page =
                Page.empty();

        // When
        SurveyUnitCompletedCsv result = presenter.present(page);

        // Then
        assertThat(result.rows()).isEmpty();
    }
}