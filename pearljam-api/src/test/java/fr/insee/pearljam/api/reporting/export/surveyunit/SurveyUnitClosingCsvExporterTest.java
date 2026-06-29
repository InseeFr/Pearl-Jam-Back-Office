package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingViewModelMapper;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
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

        verify(port, times(1)).getSurveyUnitsToClose("user-123", presenter);
    }

    @Test
    @DisplayName("Passes the exact presenter instance to the port")
    void shouldPassPresenterUnchanged() {
        exporter.export("user-abc", null, LocalDate.now(ZoneId.of("UTC")));

        verify(port).getSurveyUnitsToClose(eq("user-abc"), null, same(presenter));
    }

    @Test
    @DisplayName("Port is called exactly once per export call")
    void shouldCallPortExactlyOnce() {
        exporter.export("user-123", null, LocalDate.now(ZoneId.of("UTC")));

        verify(port, times(1)).getSurveyUnitsToClose(any(), null, any());
    }

    @Test
    @DisplayName("Different userIds are forwarded correctly to the port")
    void shouldForwardDifferentUserIds() {
        exporter.export("alice",null, LocalDate.now(ZoneId.of("UTC")));
        exporter.export("bob", null, LocalDate.now(ZoneId.of("UTC")));

        verify(port).getSurveyUnitsToClose(eq("alice"), null, any());
        verify(port).getSurveyUnitsToClose(eq("bob"), null, any());
    }
}