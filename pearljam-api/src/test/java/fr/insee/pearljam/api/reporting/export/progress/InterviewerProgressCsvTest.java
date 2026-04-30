package fr.insee.pearljam.api.reporting.export.progress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerProgressCsvTest {

    @Test
    @DisplayName("Has interviewer label as the first header")
    void shouldHaveInterviewerLabelAsFirstHeader() {
        // Given / When
        InterviewerProgressCsv csv = new InterviewerProgressCsv(List.of());

        // Then
        assertThat(csv.headers().values().getFirst()).isEqualTo(ProgressCsvHeaders.INTERVIEWER_LABEL.getHeaderName());
    }

    @Test
    @DisplayName("Has all common headers after the interviewer label and interviewer id")
    void shouldHaveAllCommonHeadersAfterInterviewerLabel() {
        // Given / When
        InterviewerProgressCsv csv = new InterviewerProgressCsv(List.of());

        // Then
        assertThat(csv.headers().values()).hasSize(15);
        assertThat(csv.headers().values()).containsExactly(
                ProgressCsvHeaders.INTERVIEWER_LABEL.getHeaderName(),
                ProgressCsvHeaders.INTERVIEWER_ID.getHeaderName(),
                ProgressCsvHeaders.PROGRESS_RATE.getHeaderName(),
                ProgressCsvHeaders.ALLOCATED.getHeaderName(),
                ProgressCsvHeaders.NOT_STARTED.getHeaderName(),
                ProgressCsvHeaders.IN_PROGRESS.getHeaderName(),
                ProgressCsvHeaders.PENDING_TRANSMISSION.getHeaderName(),
                ProgressCsvHeaders.TO_REVIEW.getHeaderName(),
                ProgressCsvHeaders.VALIDATED.getHeaderName(),
                ProgressCsvHeaders.PREPARING_CONTACT.getHeaderName(),
                ProgressCsvHeaders.WITH_CONTACT.getHeaderName(),
                ProgressCsvHeaders.WITH_APPOINTMENT.getHeaderName(),
                ProgressCsvHeaders.STARTED.getHeaderName(),
                ProgressCsvHeaders.NOTICE_LETTER.getHeaderName(),
                ProgressCsvHeaders.REMINDER_LETTER.getHeaderName()
        );
    }
}
