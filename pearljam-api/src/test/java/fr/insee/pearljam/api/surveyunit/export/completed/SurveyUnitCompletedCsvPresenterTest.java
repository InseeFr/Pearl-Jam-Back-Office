package fr.insee.pearljam.api.surveyunit.export.completed;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.lang.reflect.Method;
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

    // ==================== formatDateInFrench method tests ====================

    @Test
    void formatDateInFrench_should_return_null_when_endDate_is_null() throws Exception {
        // Given
        Method method = SurveyUnitCompletedCsvPresenter.class
                .getDeclaredMethod("formatDateInFrench", String.class);
        method.setAccessible(true);

        // When
        String result = (String) method.invoke(presenter, (String) null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void formatDateInFrench_should_return_formatted_date_when_endDate_is_valid_timestamp() throws Exception {
        // Given
        Method method = SurveyUnitCompletedCsvPresenter.class
                .getDeclaredMethod("formatDateInFrench", String.class);
        method.setAccessible(true);
        
        String timestamp = "1749544200000"; // 2025-06-10T10:30:00Z

        // When
        String result = (String) method.invoke(presenter, timestamp);

        // Then
        assertThat(result).isEqualTo("10/06/2025");
    }

    @Test
    void formatDateInFrench_should_return_original_when_endDate_is_not_valid_number() throws Exception {
        // Given
        Method method = SurveyUnitCompletedCsvPresenter.class
                .getDeclaredMethod("formatDateInFrench", String.class);
        method.setAccessible(true);
        
        String invalidTimestamp = "invalid-timestamp";

        // When
        String result = (String) method.invoke(presenter, invalidTimestamp);

        // Then
        assertThat(result).isEqualTo("invalid-timestamp");
    }

    // ==================== removeCarriageReturnsFromComment method tests ====================

    @Test
    void removeCarriageReturnsFromComment_should_return_null_when_comment_is_null() throws Exception {
        // Given
        Method method = SurveyUnitCompletedCsvPresenter.class
                .getDeclaredMethod("removeCarriageReturnsFromComment", String.class);
        method.setAccessible(true);

        // When
        String result = (String) method.invoke(presenter, (String) null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void removeCarriageReturnsFromComment_should_replace_carriage_returns_and_newlines() throws Exception {
        // Given
        Method method = SurveyUnitCompletedCsvPresenter.class
                .getDeclaredMethod("removeCarriageReturnsFromComment", String.class);
        method.setAccessible(true);
        
        String commentWithReturns = "comment\rtest \n return\r\n carriage";

        // When
        String result = (String) method.invoke(presenter, commentWithReturns);

        // Then
        assertThat(result).isEqualTo("comment test   return  carriage");
    }

    @Test
    void removeCarriageReturnsFromComment_should_return_same_string_when_no_carriage_returns() throws Exception {
        // Given
        Method method = SurveyUnitCompletedCsvPresenter.class
                .getDeclaredMethod("removeCarriageReturnsFromComment", String.class);
        method.setAccessible(true);
        
        String commentWithoutReturns = "comment test";

        // When
        String result = (String) method.invoke(presenter, commentWithoutReturns);

        // Then
        assertThat(result).isEqualTo("comment test");
    }

    // ==================== empty method tests ====================

    @Test
    void empty_should_return_empty_SurveyUnitCompletedCsv() {
        // When
        SurveyUnitCompletedCsv result = presenter.empty();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.rows()).isEmpty();
    }
}