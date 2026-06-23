package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.springframework.data.domain.Page;

/**
 * Presenter interface for formatting survey units assigned responses.
 * Implementations of this interface are responsible for transforming the domain model
 * into the appropriate response format.
 *
 * @param <T> the type of response this presenter produces
 */
public interface SurveyUnitAssignedPresenter<T> {

    /**
     * Presents the paginated survey units to review in the appropriate response format.
     *
     * @param surveyUnits the paginated survey units to present
     * @return the formatted response
     */
    T present(Page<SurveyUnitAssigned> surveyUnits);
}