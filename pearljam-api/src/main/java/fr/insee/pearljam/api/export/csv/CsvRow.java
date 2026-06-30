package fr.insee.pearljam.api.export.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CsvRow {
    private static final String SEPARATOR = ";";
    private static final String LINE_END = "\r\n";

    private final List<String> values;

    private CsvRow(List<String> values) {
        this.values = values;
    }

    public List<String> values() {
        return values;
    }

    public static CsvRow from(Object... values) {
        List<String> csvValues = Arrays.stream(values)
                .map(CsvRow::normalize)
                .toList();
        return new CsvRow(csvValues);
    }

    private static String normalize(Object value) {
        String normalized = value == null ? "" : String.valueOf(value);
        if (normalized.contains(SEPARATOR) || normalized.contains("\"")) {
            normalized = "\"" + normalized.replace("\"", "\"\"") + "\"";
        }
        return normalized;
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
