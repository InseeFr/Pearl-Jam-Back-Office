package fr.insee.pearljam.api.export.csv;

import java.util.List;

public interface CsvExportable {
    CsvRow headers();
    List<CsvRow> rows();
}
