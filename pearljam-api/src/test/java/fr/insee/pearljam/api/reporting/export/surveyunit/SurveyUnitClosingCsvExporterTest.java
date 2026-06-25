package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyUnitClosingCsvExporterTest {

    @Mock
    private SurveyUnitClosingApiCsvPresenter presenter;

    @Mock
    private SurveyUnitClosingPort surveyUnitClosingPort;

    private SurveyUnitClosingCsvExporter exporter;

    @BeforeEach
    void setUp() {
        exporter = new SurveyUnitClosingCsvExporter(presenter, surveyUnitClosingPort);
    }

    // ------------------------------------------------------------------
    // Delegation to port
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Delegation")
    class Delegation {

        @Test
        @DisplayName("calls getSurveyUnitsToClose with the given userId and the presenter")
        void shouldDelegateToPortWithCorrectArguments() {
            SurveyUnitClosingCsv emptyCsv = new SurveyUnitClosingCsv(List.of());
            when(surveyUnitClosingPort.getSurveyUnitsToClose(eq("user-123"), eq(presenter)))
                    .thenReturn(emptyCsv);

            exporter.export("user-123");

            verify(surveyUnitClosingPort, times(1))
                    .getSurveyUnitsToClose("user-123", presenter);
        }

        @Test
        @DisplayName("passes the presenter instance unchanged to the port")
        void shouldPassPresenterUnchanged() {
            SurveyUnitClosingCsv emptyCsv = new SurveyUnitClosingCsv(List.of());
            ArgumentCaptor<SurveyUnitClosingApiCsvPresenter> captor =
                    ArgumentCaptor.forClass(SurveyUnitClosingApiCsvPresenter.class);

            when(surveyUnitClosingPort.getSurveyUnitsToClose(any(), captor.capture()))
                    .thenReturn(emptyCsv);

            exporter.export("user-abc");

            assertThat(captor.getValue()).isSameAs(presenter);
        }

        @Test
        @DisplayName("never calls the presenter directly")
        void shouldNeverCallPresenterDirectly() {
            SurveyUnitClosingCsv emptyCsv = new SurveyUnitClosingCsv(List.of());
            when(surveyUnitClosingPort.getSurveyUnitsToClose(any(), any()))
                    .thenReturn(emptyCsv);

            exporter.export("user-123");

            verifyNoInteractions(presenter);
        }
    }

    // ------------------------------------------------------------------
    // User ID propagation
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("userId propagation")
    class UserIdPropagation {

        @Test
        @DisplayName("different userIds are forwarded correctly to the port")
        void shouldForwardDifferentUserIds() {
            SurveyUnitClosingCsv csv = new SurveyUnitClosingCsv(List.of());
            when(surveyUnitClosingPort.getSurveyUnitsToClose(any(), any())).thenReturn(csv);

            exporter.export("alice");
            exporter.export("bob");

            verify(surveyUnitClosingPort).getSurveyUnitsToClose(eq("alice"), any());
            verify(surveyUnitClosingPort).getSurveyUnitsToClose(eq("bob"), any());
        }
    }
}