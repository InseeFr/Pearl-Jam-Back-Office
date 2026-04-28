package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.SurveyUnitToReviewDto;
import fr.insee.pearljam.api.reporting.response.SurveyUnitToReviewResponse;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitToReviewPresenterTest {

    private SurveyUnitToReviewPresenter presenter;
    private static final String DATACOLLECTION_UI_URL = "https://datacollection-ui";

    @BeforeEach
    void setUp() {
        presenter = new SurveyUnitToReviewPresenter();
        // Inject the datacollectionUiUrl using reflection since it's a private field
        try {
            var field = SurveyUnitToReviewPresenter.class.getDeclaredField("datacollectionUiUrl");
            field.setAccessible(true);
            field.set(presenter, DATACOLLECTION_UI_URL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set datacollectionUiUrl field", e);
        }
    }

    @Test
    @DisplayName("Should map SurveyUnitToReview page to SurveyUnitToReviewResponse")
    void shouldMapSurveyUnitToReviewPageToResponse() {
        // Given
        List<SurveyUnitToReview> surveyUnits = List.of(
                createSurveyUnitToReview("SU-1", "Campaign 2024", "CONTACTED", "INT-001", "John Doe", false, "First comment"),
                createSurveyUnitToReview("SU-2", "Campaign 2024", "NOT_CONTACTED", "INT-002", "Jane Smith", true, "Second comment")
        );

        Page<SurveyUnitToReview> page = new PageImpl<>(surveyUnits, PageRequest.of(0, 10), 20);

        // When
        SurveyUnitToReviewResponse result = presenter.present(page);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);

        assertThat(result.content().get(0)).satisfies(dto -> {
            assertThat(dto.id()).isEqualTo("SU-1");
            assertThat(dto.campaignLabel()).isEqualTo("Campaign 2024");
            assertThat(dto.contactOutcome()).isEqualTo("CONTACTED");
            assertThat(dto.interviewerNameLabel()).isEqualTo("John Doe");
            assertThat(dto.viewed()).isFalse();
            assertThat(dto.readOnlyUrl()).isEqualTo(DATACOLLECTION_UI_URL + "/review/interrogations/SU-1");
            assertThat(dto.lastComment()).isEqualTo("First comment");
        });

        assertThat(result.content().get(1)).satisfies(dto -> {
            assertThat(dto.id()).isEqualTo("SU-2");
            assertThat(dto.campaignLabel()).isEqualTo("Campaign 2024");
            assertThat(dto.contactOutcome()).isEqualTo("NOT_CONTACTED");
            assertThat(dto.interviewerNameLabel()).isEqualTo("Jane Smith");
            assertThat(dto.viewed()).isTrue();
            assertThat(dto.readOnlyUrl()).isEqualTo(DATACOLLECTION_UI_URL + "/review/interrogations/SU-2");
            assertThat(dto.lastComment()).isEqualTo("Second comment");
        });

        // Verify pagination information
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(20);
        assertThat(result.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle empty page correctly")
    void shouldHandleEmptyPage() {
        // Given
        Page<SurveyUnitToReview> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        // When
        SurveyUnitToReviewResponse result = presenter.present(emptyPage);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).isEmpty();
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }

    @Test
    @DisplayName("Should handle null values in SurveyUnitToReview fields")
    void shouldHandleNullValues() {
        // Given
        List<SurveyUnitToReview> surveyUnits = List.of(
                createSurveyUnitToReview("SU-NULL", null, null, null, null, null, null)
        );

        Page<SurveyUnitToReview> page = new PageImpl<>(surveyUnits, PageRequest.of(0, 5), 1);

        // When
        SurveyUnitToReviewResponse result = presenter.present(page);

        // Then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).satisfies(dto -> {
            assertThat(dto.id()).isEqualTo("SU-NULL");
            assertThat(dto.campaignLabel()).isNull();
            assertThat(dto.contactOutcome()).isNull();
            assertThat(dto.interviewerNameLabel()).isNull();
            assertThat(dto.viewed()).isNull();
            assertThat(dto.readOnlyUrl()).isEqualTo(DATACOLLECTION_UI_URL + "/review/interrogations/SU-NULL");
            assertThat(dto.lastComment()).isNull();
        });

        // Verify pagination information
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should construct readOnlyUrl correctly")
    void shouldConstructReadOnlyUrlCorrectly() {
        // Given
        List<SurveyUnitToReview> surveyUnits = List.of(
                createSurveyUnitToReview("SU-TEST-123", "Test Campaign", "TEST_OUTCOME", "TEST-INT", "Test Interviewer", true, "Test comment")
        );

        Page<SurveyUnitToReview> page = new PageImpl<>(surveyUnits);

        // When
        SurveyUnitToReviewResponse result = presenter.present(page);

        // Then
        SurveyUnitToReviewDto dto = result.content().getFirst();
        assertThat(dto.readOnlyUrl()).isEqualTo(DATACOLLECTION_UI_URL + "/review/interrogations/SU-TEST-123");
    }

    private SurveyUnitToReview createSurveyUnitToReview(String id, String campaignLabel, String contactOutcome,
                                                       String interviewerId, String interviewerName,
                                                       Boolean viewed, String lastComment) {
        return new SurveyUnitToReview(id, campaignLabel, contactOutcome, interviewerId, interviewerName, viewed, lastComment);
    }
}