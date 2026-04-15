package fr.insee.pearljam.api.reporting.export.csv;

import java.util.Arrays;
import java.util.List;

public record CsvRow(List<String> values) {
    private static final String SEPARATOR = ";";
    private static final String LINE_END = "\r\n";

    public static CsvRow from(Object... values) {
        List<String> csvValues = Arrays.stream(values)
                .map(value -> value == null ? "" : String.valueOf(value))
                .map(value -> {
                            if (value.contains(SEPARATOR) || value.contains("\"")) {
                                return "\"" + value.replace("\"", "\"\"") + "\"";
                            }
                            return value;
                        }
                )
                .toList();
        return new CsvRow(csvValues);
    }

    public String toCsvLine() {
        return String.join(SEPARATOR, this.values) + LINE_END;
    }
}
