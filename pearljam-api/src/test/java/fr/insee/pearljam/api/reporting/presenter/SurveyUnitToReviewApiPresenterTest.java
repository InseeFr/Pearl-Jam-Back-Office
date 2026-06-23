package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitToReviewApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToReviewPageResponse;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToReviewResponse;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitToReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitToReviewApiPresenterTest {

    private SurveyUnitToReviewApiPresenter presenter;
    private static final String DATACOLLECTION_UI_URL = "https://datacollection-ui";

    @BeforeEach
    void setUp() {
        presenter = new SurveyUnitToReviewApiPresenter();
        // Inject the datacollectionUiUrl using reflection since it's a private field
        try {
            var field = SurveyUnitToReviewApiPresenter.class.getDeclaredField("datacollectionUiUrl");
            field.setAccessible(true);
            field.set(presenter, DATACOLLECTION_UI_URL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set datacollectionUiUrl field", e);
        }
    }

    @Test
    @DisplayName("Should map SurveyUnitToReview page to SurveyUnitToReviewPageResponse")
    void shouldMapSurveyUnitToReviewPageToResponse() {
        // Given
        List<SurveyUnitToReview> surveyUnits = List.of(
                createSurveyUnitToReview("SU-1", "Survey unit 1", "Campaign 2024", "CONTACTED", "INT-001", "John","Doe", false, "First comment"),
                createSurveyUnitToReview("SU-2", "Survey unit 2","Campaign 2024", "NOT_CONTACTED", "INT-002", "Jane", "Smith", true, "Second comment")
        );

        Page<SurveyUnitToReview> page = new PageImpl<>(surveyUnits, PageRequest.of(0, 10), 20);

        // When
        SurveyUnitToReviewPageResponse result = presenter.present(page);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);

        assertThat(result.content().get(0)).satisfies(dto -> {
            assertThat(dto.id()).isEqualTo("SU-1");
            assertThat(dto.surveyUnitDisplayName()).isEqualTo("Survey unit 1");

            assertThat(dto.campaignLabel()).isEqualTo("Campaign 2024");
            assertThat(dto.contactOutcome()).isEqualTo("CONTACTED");
            assertThat(dto.interviewerLabel()).isEqualTo("John Doe");
            assertThat(dto.viewed()).isFalse();
            assertThat(dto.readOnlyUrl()).isEqualTo(DATACOLLECTION_UI_URL + "/review/interrogations/SU-1");
            assertThat(dto.lastComment()).isEqualTo("First comment");
        });

        assertThat(result.content().get(1)).satisfies(dto -> {
            assertThat(dto.id()).isEqualTo("SU-2");
            assertThat(dto.surveyUnitDisplayName()).isEqualTo("Survey unit 2");
            assertThat(dto.campaignLabel()).isEqualTo("Campaign 2024");
            assertThat(dto.contactOutcome()).isEqualTo("NOT_CONTACTED");
            assertThat(dto.interviewerLabel()).isEqualTo("Jane Smith");
            assertThat(dto.viewed()).isTrue();
            assertThat(dto.readOnlyUrl()).isEqualTo(DATACOLLECTION_UI_URL + "/review/interrogations/SU-2");
            assertThat(dto.lastComment()).isEqualTo("Second comment");
        });

        // Verify pagination information
        assertThat(result.page()).isZero();
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
        SurveyUnitToReviewPageResponse result = presenter.present(emptyPage);

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
                createSurveyUnitToReview("SU-NULL", null,null, null, null, null, null,null, null)
        );

        Page<SurveyUnitToReview> page = new PageImpl<>(surveyUnits, PageRequest.of(0, 5), 1);

        // When
        SurveyUnitToReviewPageResponse result = presenter.present(page);

        // Then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).satisfies(dto -> {
            assertThat(dto.id()).isEqualTo("SU-NULL");
            assertThat(dto.surveyUnitDisplayName()).isNull();
            assertThat(dto.campaignLabel()).isNull();
            assertThat(dto.contactOutcome()).isNull();
            assertThat(dto.interviewerLabel()).isNull();
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
                createSurveyUnitToReview("SU-TEST-123", "Survey unit test 123", "Test Campaign", "TEST_OUTCOME", "TEST-INT", "Test", "Interviewer", true,"Test comment")
        );

        Page<SurveyUnitToReview> page = new PageImpl<>(surveyUnits);

        // When
        SurveyUnitToReviewPageResponse result = presenter.present(page);

        // Then
        SurveyUnitToReviewResponse dto = result.content().getFirst();
        assertThat(dto.readOnlyUrl()).isEqualTo(DATACOLLECTION_UI_URL + "/review/interrogations/SU-TEST-123");
    }

    private SurveyUnitToReview createSurveyUnitToReview(String id,
                                                        String surveyUnitDisplayName,
                                                        String campaignLabel,
                                                        String contactOutcome,
                                                        String interviewerId,
                                                        String interviewerFirstName, String interviewerLastName,
                                                       Boolean viewed, String lastComment) {
        return new SurveyUnitToReview(id, surveyUnitDisplayName, campaignLabel, contactOutcome, interviewerId, interviewerFirstName, interviewerLastName, viewed, lastComment);
    }
}