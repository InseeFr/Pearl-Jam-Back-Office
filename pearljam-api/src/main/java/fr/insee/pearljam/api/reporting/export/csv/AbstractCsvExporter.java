package fr.insee.pearljam.api.reporting.export.csv;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractCsvExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    protected final ResponseEntity<byte[]> buildResponse(CsvExportable csvData, String label, LocalDate date) {
        String csvContent = generateCsvContent(csvData);
        String filename = generateFilename(label, date);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent.getBytes());
    }

    private String generateCsvContent(CsvExportable data) {
        try (StringWriter writer = new StringWriter()) {
            writer.write("\uFEFF"); // BOM for Excel
            writer.write(data.headers().toCsvLine());
            for (CsvRow row : data.rows()) {
                writer.write(row.toCsvLine());
            }
            return writer.toString();
        } catch (IOException e) {
            throw new CsvGenerationException("Failed to generate CSV with headers: " + e.getMessage(), e);
        }
    }

    private String generateFilename(String label, LocalDate date) {
        return label + "_" + date.format(DATE_FORMAT) + ".csv";
    }
}
