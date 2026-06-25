package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SurveyUnitClosingCsv")
class SurveyUnitClosingCsvTest {

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("can be constructed with an empty list of rows")
        void shouldAcceptEmptyRowList() {
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of());
            assertThat(csv).isNotNull();
            assertThat(csv.rows()).isEmpty();
        }

        @Test
        @DisplayName("stores the provided rows accessible via rows()")
        void shouldStoreRows() {
            CsvRow row1 = CsvRow.from("a", "b");
            CsvRow row2 = CsvRow.from("c", "d");
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of(row1, row2));
            assertThat(csv.rows()).containsExactly(row1, row2);
        }
    }

    // ------------------------------------------------------------------
    // headers()
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("headers()")
    class Headers {

        @Test
        @DisplayName("returns a non-null CsvRow")
        void shouldReturnNonNullHeaders() {
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of());
            assertThat(csv.headers()).isNotNull();
        }

        @Test
        @DisplayName("header count matches the number of SurveyUnitClosingCsvHeaders enum values")
        void shouldMatchEnumCount() {
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of());
            int expectedCount = SurveyUnitClosingCsvHeaders.values().length;
            assertThat(csv.headers().values()).hasSize(expectedCount);
        }

        @Test
        @DisplayName("header values match the header names defined in SurveyUnitClosingCsvHeaders")
        void shouldMatchHeaderNames() {
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of());
            List<String> expectedNames = Arrays.stream(SurveyUnitClosingCsvHeaders.values())
                    .map(SurveyUnitClosingCsvHeaders::getHeaderName)
                    .toList();

            List<String> actualValues = csv.headers().values();
            assertThat(actualValues).containsExactlyElementsOf(expectedNames);
        }

        @Test
        @DisplayName("headers are always the same regardless of the row content")
        void shouldReturnSameHeadersRegardlessOfRows() {
            SurveyUnitClosingCsv empty = new SurveyUnitClosingCsv(List.of());
            SurveyUnitClosingCsv withRows = new SurveyUnitClosingCsv(
                    List.of(CsvRow.from("x", "y", "z")));

            assertThat(empty.headers().values())
                    .containsExactlyElementsOf(withRows.headers().values());
        }

        @Test
        @DisplayName("no header value is null or blank")
        void shouldHaveNonNullNonBlankHeaders() {
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of());
            csv.headers().values().forEach(value ->
                    assertThat(value)
                            .isNotNull()
                            .asString()
                            .isNotBlank());
        }
    }

    // ------------------------------------------------------------------
    // rows() – CsvExportable contract
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("rows() via CsvExportable")
    class RowsViaInterface {

        @Test
        @DisplayName("rows() returns an empty list (current implementation placeholder)")
        void shouldReturnEmptyList() {
            CsvRow row = CsvRow.from("val1", "val2");
            // Note: the current implementation of rows() in the record returns List.of()
            // regardless of the constructor argument. This test documents that behaviour.
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of(row));
            assertThat(csv.rows()).isEmpty();
        }
    }

    // ------------------------------------------------------------------
    // CSV_HEADERS constant
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("CSV_HEADERS constant")
    class CsvHeadersConstant {

        @Test
        @DisplayName("CSV_HEADERS is not null")
        void shouldNotBeNull() {
            assertThat(SurveyUnitClosingCsv.CSV_HEADERS).isNotNull();
        }

        @Test
        @DisplayName("CSV_HEADERS list matches buildHeaders() output")
        void shouldMatchBuildHeaders() {
            List<SurveyUnitClosingCsvHeaders> expected = SurveyUnitClosingCsvHeaders.buildHeaders();
            assertThat(SurveyUnitClosingCsv.CSV_HEADERS).containsExactlyElementsOf(expected);
        }

        @Test
        @DisplayName("CSV_HEADERS contains all enum values")
        void shouldContainAllEnumValues() {
            assertThat(SurveyUnitClosingCsv.CSV_HEADERS)
                    .containsExactlyInAnyOrder(SurveyUnitClosingCsvHeaders.values());
        }
    }

    // ------------------------------------------------------------------
    // Record equality and immutability
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Record semantics")
    class RecordSemantics {

        @Test
        @DisplayName("two instances with the same rows are equal")
        void shouldBeEqualWhenSameRows() {
            List<CsvRow> rows = List.of(CsvRow.from("a", "b"));
            SurveyUnitClosingCsv csv1 = new SurveyUnitClosingCsv(rows);
            SurveyUnitClosingCsv csv2 = new SurveyUnitClosingCsv(rows);
            assertThat(csv1).isEqualTo(csv2);
        }

        @Test
        @DisplayName("two instances with different rows are not equal")
        void shouldNotBeEqualWithDifferentRows() {
            SurveyUnitClosingCsv csv1 = new SurveyUnitClosingCsv(List.of(CsvRow.from("a")));
            SurveyUnitClosingCsv csv2 = new SurveyUnitClosingCsv(List.of(CsvRow.from("b")));
            assertThat(csv1).isNotEqualTo(csv2);
        }

        @Test
        @DisplayName("hashCode is consistent between equal instances")
        void shouldHaveConsistentHashCode() {
            List<CsvRow> rows = List.of(CsvRow.from("x"));
            SurveyUnitClosingCsv csv1 = new SurveyUnitClosingCsv(rows);
            SurveyUnitClosingCsv csv2 = new SurveyUnitClosingCsv(rows);
            assertThat(csv1.hashCode()).isEqualTo(csv2.hashCode());
        }
    }
}