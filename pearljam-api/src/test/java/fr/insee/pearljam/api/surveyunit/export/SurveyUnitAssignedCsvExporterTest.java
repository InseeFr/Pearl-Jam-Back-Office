package fr.insee.pearljam.api.surveyunit.export;

import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitAssignedCsvPresenter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SurveyUnitAssignedCsvExporterTest {

    private final SurveyUnitAssignedPort stubbedPort = new SurveyUnitAssignedPort() {
        @Override
        public <T> T getSurveyUnitsAssigned(
                String userId, String campaignId, String search, Pageable pageable,
                SurveyUnitAssignedPresenter<T> presenter) {
            return presenter.present(new PageImpl<>(List.of(fooSurveyUnitAssigned())));
        }
    };

    private final SurveyUnitAssignedCsvPresenter csvPresenter = new SurveyUnitAssignedCsvPresenter();

    private static @NonNull SurveyUnitAssigned fooSurveyUnitAssigned() {
        return new SurveyUnitAssigned(
                "foo-id",
                "FOO_LABEL",
                "1",
                "John",
                "Doe",
                "33",
                "City",
                "Foo state",
                "-");
    }

    private SurveyUnitAssignedCsvExporter csvExporter;

    @BeforeEach
    void setUp() {
        csvExporter = new SurveyUnitAssignedCsvExporter(csvPresenter, stubbedPort);
    }

    @Test
    void fileNameTest() {
        ResponseEntity<byte[]> response = csvExporter.export("foo-user-id", "FOO_CAMPAIGN", "");

        String responseFileName = response.getHeaders().getContentDisposition().getFilename();
        assertNotNull(responseFileName);
        String expectedFilename = "^FOO_CAMPAIGN_UE_confiees_[0-9]{8}.csv$";
        assertTrue(Pattern.matches(expectedFilename, responseFileName));
    }

    @Test
    void fileNameTest_withSearch() {
        ResponseEntity<byte[]> response = csvExporter.export("foo-user-id", "FOO_CAMPAIGN", "search value");

        String responseFilename = response.getHeaders().getContentDisposition().getFilename();
        assertNotNull(responseFilename);
        String expectedFilename = "^FOO_CAMPAIGN_UE_confiees_filtre_search_value_[0-9]{8}.csv$";
        assertTrue(Pattern.matches(expectedFilename, responseFilename));
    }

    @Test
    void responseCsvContent() {
        ResponseEntity<byte[]> response = csvExporter.export("foo-user-id", "FOO_CAMPAIGN", "");

        assertNotNull(response.getBody());
        String responseBody = new String(response.getBody());
        String[] responseRows = responseBody.split(System.lineSeparator());
        assertEquals(2, responseRows.length);
        String expectedHeadersRow = "﻿Identifiant technique;Identifiant de l'ue;Nom Prénom enquêteur;Ssech;Département;Commune;Etat de l'UE;Motif provisoire";
        String expectedContentRow = "foo-id;FOO_LABEL;John Doe;1;33;City;Foo state;-";
        assertThat(expectedHeadersRow).isEqualToIgnoringNewLines(responseRows[0]);
        assertThat(expectedContentRow).isEqualToIgnoringNewLines(responseRows[1]);
    }

}
