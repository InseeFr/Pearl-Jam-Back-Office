package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitToReview;
import org.springframework.data.domain.Page;

/**
 * Presenter interface for formatting survey units to review responses.
 * Implementations of this interface are responsible for transforming the domain model
 * into the appropriate response format.
 *
 * @param <T> the type of response this presenter produces
 */
public interface SurveyUnitToReviewPresenter<T> {

    /**
     * Presents the paginated survey units to review in the appropriate response format.
     *
     * @param surveyUnits the paginated survey units to present
     * @return the formatted response
     */
    T present(Page<SurveyUnitToReview> surveyUnits);
}