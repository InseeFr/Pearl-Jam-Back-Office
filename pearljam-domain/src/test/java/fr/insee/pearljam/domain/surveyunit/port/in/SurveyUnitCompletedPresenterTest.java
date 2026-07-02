package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchedByStatesAndCampaignIdViewBuilder.aSurveyUnit;
import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitCompletedPresenterTest {

    private SurveyUnitCompletedPresenter<Void> presenter;
    @BeforeEach
    void setUp() {
        presenter = new SurveyUnitCompletedPresenter<>() {
            @Override
            public Void present(org.springframework.data.domain.Page<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits) {
                return null;
            }

            @Override
            public Void empty() {
                return null;
            }
        };
    }
    @Test
    void shouldReturnFullName() {
        var su = aSurveyUnit()
                .withFirstName("John")
                .withLastName("Doe")
                .build();

        assertThat(presenter.getInterviewerLabel(su))
                .isEqualTo("John Doe");
    }

    @Test
    void shouldReturnFirstNameWhenLastNameIsNull() {
        var su = aSurveyUnit()
                .withLastName(null)
                .build();

        assertThat(presenter.getInterviewerLabel(su))
                .isEqualTo("John");
    }

    @Test
    void shouldReturnLastNameWhenFirstNameIsNull() {
        var su = aSurveyUnit()
                .withFirstName(null)
                .build();

        assertThat(presenter.getInterviewerLabel(su))
                .isEqualTo("Doe");
    }

    @Test
    void shouldReturnEmptyWhenBothNamesAreNull() {
        var su = aSurveyUnit()
                .withFirstName(null)
                .withLastName(null)
                .build();

        assertThat(presenter.getInterviewerLabel(su))
                .isEmpty();
    }


}
