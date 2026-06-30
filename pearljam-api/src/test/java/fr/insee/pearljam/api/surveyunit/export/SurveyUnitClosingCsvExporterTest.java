package fr.insee.pearljam.api.surveyunit.export;

import fr.insee.pearljam.api.surveyunit.export.closing.SurveyUnitClosingApiCsvPresenter;
import fr.insee.pearljam.api.surveyunit.export.closing.SurveyUnitClosingCsv;
import fr.insee.pearljam.api.surveyunit.export.closing.SurveyUnitClosingCsvExporter;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingViewModelMapper;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SurveyUnitClosingCsvExporterTest {

    private SurveyUnitClosingApiCsvPresenter presenter;
    private SurveyUnitClosingPort port;
    private SurveyUnitClosingCsvExporter exporter;

    @BeforeEach
    void setup() {
        presenter = new SurveyUnitClosingApiCsvPresenter(new SurveyUnitClosingViewModelMapper());
        port = mock(SurveyUnitClosingPort.class);
        when(port.getSurveyUnitsToClose(any(), any(), any())).thenReturn(new SurveyUnitClosingCsv(List.of()));
        exporter = new SurveyUnitClosingCsvExporter(presenter, port);
    }

    @Test
    @DisplayName("Calls getSurveyUnitsToClose with the given userId and the presenter")
    void shouldDelegateToPortWithCorrectUserId() {
        exporter.export("user-123", null, LocalDate.now(ZoneId.of("UTC")));

        verify(port, times(1)).getSurveyUnitsToClose("user-123", null, presenter);
    }

    @Test
    @DisplayName("Passes the exact presenter instance to the port")
    void shouldPassPresenterUnchanged() {
        exporter.export("user-abc", null, LocalDate.now(ZoneId.of("UTC")));

        verify(port).getSurveyUnitsToClose(eq("user-abc"), any(), same(presenter));
    }

    @Test
    @DisplayName("Port is called exactly once per export call")
    void shouldCallPortExactlyOnce() {
        exporter.export("user-123", null, LocalDate.now(ZoneId.of("UTC")));

        verify(port, times(1)).getSurveyUnitsToClose(any(), any(), any());
    }

    @Test
    @DisplayName("Different userIds are forwarded correctly to the port")
    void shouldForwardDifferentUserIds() {
        exporter.export("alice",null, LocalDate.now(ZoneId.of("UTC")));
        exporter.export("bob", null, LocalDate.now(ZoneId.of("UTC")));

        verify(port).getSurveyUnitsToClose(eq("alice"), any(), any());
        verify(port).getSurveyUnitsToClose(eq("bob"), any(), any());
    }
}