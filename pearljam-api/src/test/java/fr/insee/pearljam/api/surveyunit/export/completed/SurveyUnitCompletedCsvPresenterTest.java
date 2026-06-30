package fr.insee.pearljam.api.surveyunit.export.completed;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
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
                null, // interviewer (adapter selon ton modèle)
                LocalDate.of(2025, 6, 10).toString(),
                "CONTACTED",
                "COMPLETED",
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
        SU001;Survey Unit 1;2025-06-10;;CONTACTED;COMPLETED;INA;true;comment
        """
        );

    }

    @Test
    void should_return_empty_string_when_viewed_is_null() {
        // Given
        SurveyUnitFetchedByStatesAndCampaignIdView surveyUnit = new SurveyUnitFetchedByStatesAndCampaignIdView(
                "SU001",
                "Survey Unit 1",
                null,
                LocalDate.of(2025, 6, 10).toString(),
                "CONTACTED",
                "COMPLETED",
                "INA",
                null,
                "comment"
        );

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> page =
                new PageImpl<>(List.of(surveyUnit));

        // When
        SurveyUnitCompletedCsv result = presenter.present(page);

        // Then
        CsvRow row = result.rows().getFirst();

        assertThat(row.toCsvLine()).isEqualToIgnoringNewLines("""
        SU001;Survey Unit 1;2025-06-10;;CONTACTED;COMPLETED;INA;;comment
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