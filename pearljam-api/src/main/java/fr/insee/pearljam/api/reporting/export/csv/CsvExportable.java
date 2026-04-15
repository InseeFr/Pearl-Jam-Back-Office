package fr.insee.pearljam.api.reporting.export.csv;

import java.util.List;

public interface CsvExportable {
    CsvRow headers();
    List<CsvRow> rows();
}
