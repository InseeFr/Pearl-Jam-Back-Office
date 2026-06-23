package fr.insee.pearljam.api.reporting.export.csv;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public static List<Object> emptyRowWithValueAtSpecificPosition(Object value, int positon, int columnCount) {
        List<Object> row = new ArrayList<>(Collections.nCopies(columnCount, null).stream().toList());
        row.set(positon, value);
        return row;
    }

    public String toCsvLine() {
        return String.join(SEPARATOR, this.values) + LINE_END;
    }

    public static void addRowWithTitleLabel(List<CsvRow> rows, Object columnTitleLabel, List<Object> values) {
        List<Object> rowData = new ArrayList<>();
        rowData.add(columnTitleLabel);
        rowData.addAll(values);
        rows.add(CsvRow.from(rowData.toArray()));
    }

    public static void addRowWithMultipleTitleLabel(List<CsvRow> rows, List<Object> columnTitleLabels, List<Object> values) {
        List<Object> rowData = new ArrayList<>();
        rowData.addAll(columnTitleLabels);
        rowData.addAll(values);
        rows.add(CsvRow.from(rowData.toArray()));
    }
}
