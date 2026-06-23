package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;

/**
 * Presenter interface for formatting survey unit history responses.
 * Implementations of this interface are responsible for transforming the domain model
 * into the appropriate response format.
 *
 * @param <T> the type of response this presenter produces
 */
public interface SurveyUnitHistoryPresenter<T> {

    /**
     * Presents the history of a survey units (states and communications)
     *
     * @param surveyUnitHistory the survey unit history
     * @return the formatted response
     */
    T present(SurveyUnitHistory surveyUnitHistory);
}