package fr.insee.pearljam.api.reporting.export.csv;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvRowTest {

    @Test
    void shouldJoinValuesWithSemicolon() {
        CsvRow row = CsvRow.from("a", "b", "c");
        assertThat(row.toCsvLine()).isEqualTo("a;b;c\r\n");
    }

    @Test
    void shouldConvertNullToEmptyString() {
        CsvRow row = CsvRow.from("a", null, "c");
        assertThat(row.values()).containsExactly("a", "", "c");
    }

    @Test
    void shouldConvertNonStringValues() {
        CsvRow row = CsvRow.from(42, 3.14f, 100L);
        assertThat(row.values()).containsExactly("42", "3.14", "100");
    }

    @Test
    void shouldEscapeValueContainingSemicolon() {
        CsvRow row = CsvRow.from("hello;world");
        assertThat(row.values()).containsExactly("\"hello;world\"");
    }

    @Test
    void shouldEscapeValueContainingDoubleQuotes() {
        CsvRow row = CsvRow.from("say \"hello\"");
        assertThat(row.values()).containsExactly("\"say \"\"hello\"\"\"");
    }

    @Test
    void shouldHandleEmptyRow() {
        CsvRow row = CsvRow.from();
        assertThat(row.toCsvLine()).isEqualTo("\r\n");
    }
}
