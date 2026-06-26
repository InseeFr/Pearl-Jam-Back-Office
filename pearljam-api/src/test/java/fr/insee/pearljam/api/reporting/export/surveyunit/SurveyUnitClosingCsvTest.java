package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitClosingCsvTest {

    @Test
    @DisplayName("Can be constructed with an empty list of rows")
    void shouldAcceptEmptyRowList() {
        SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    @DisplayName("Stores the provided rows accessible via rows()")
    void shouldStoreRows() {
        CsvRow row1 = CsvRow.from("a", "b");
        CsvRow row2 = CsvRow.from("c", "d");
        SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of(row1, row2));
        assertThat(csv.rows()).containsExactly(row1, row2);
    }

    @Test
    @DisplayName("headers() returns a non-null CsvRow")
    void shouldReturnNonNullHeaders() {
        assertThat(new SurveyUnitClosingCsv(List.of()).headers()).isNotNull();
    }

    @Test
    @DisplayName("Header count matches the number of SurveyUnitClosingCsvHeaders enum values")
    void shouldMatchEnumCount() {
        int expectedCount = SurveyUnitClosingCsvHeaders.values().length;
        assertThat(new SurveyUnitClosingCsv(List.of()).headers().values()).hasSize(expectedCount);
    }

    @Test
    @DisplayName("Header values match the header names defined in SurveyUnitClosingCsvHeaders")
    void shouldMatchHeaderNames() {
        List<String> expected = Arrays.stream(SurveyUnitClosingCsvHeaders.values())
                .map(SurveyUnitClosingCsvHeaders::getHeaderName)
                .toList();
        assertThat(new SurveyUnitClosingCsv(List.of()).headers().values())
                .containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("Headers are the same regardless of row content")
    void shouldReturnSameHeadersRegardlessOfRows() {
        SurveyUnitClosingCsv empty = new SurveyUnitClosingCsv(List.of());
        SurveyUnitClosingCsv withRows = new SurveyUnitClosingCsv(List.of(CsvRow.from("x", "y")));
        assertThat(empty.headers().values()).containsExactlyElementsOf(withRows.headers().values());
    }

    @Test
    @DisplayName("No header value is null or blank")
    void shouldHaveNonNullNonBlankHeaders() {
        new SurveyUnitClosingCsv(List.of()).headers().values().forEach(value ->
                assertThat(value).isNotNull().asString().isNotBlank());
    }

    @Test
    @DisplayName("CSV_HEADERS is not null")
    void shouldNotBeNull() {
        assertThat(SurveyUnitClosingCsv.CSV_HEADERS).isNotNull();
    }

    @Test
    @DisplayName("CSV_HEADERS matches buildHeaders() output")
    void shouldMatchBuildHeaders() {
        assertThat(SurveyUnitClosingCsv.CSV_HEADERS)
                .containsExactlyElementsOf(SurveyUnitClosingCsvHeaders.buildHeaders());
    }

    @Test
    @DisplayName("CSV_HEADERS contains all enum values")
    void shouldContainAllEnumValues() {
        assertThat(SurveyUnitClosingCsv.CSV_HEADERS)
                .containsExactlyInAnyOrder(SurveyUnitClosingCsvHeaders.values());
    }

    @Test
    @DisplayName("Two instances with the same rows are equal")
    void shouldBeEqualWhenSameRows() {
        List<CsvRow> rows = List.of(CsvRow.from("a", "b"));
        assertThat(new SurveyUnitClosingCsv(rows)).isEqualTo(new SurveyUnitClosingCsv(rows));
    }

    @Test
    @DisplayName("Two instances with different rows are not equal")
    void shouldNotBeEqualWithDifferentRows() {
        assertThat(new SurveyUnitClosingCsv(List.of(CsvRow.from("a"))))
                .isNotEqualTo(new SurveyUnitClosingCsv(List.of(CsvRow.from("b"))));
    }

    @Test
    @DisplayName("hashCode is consistent between equal instances")
    void shouldHaveConsistentHashCode() {
        List<CsvRow> rows = List.of(CsvRow.from("x"));
        assertThat(new SurveyUnitClosingCsv(rows).hashCode())
                .hasSameHashCodeAs(new SurveyUnitClosingCsv(rows).hashCode());
    }
}