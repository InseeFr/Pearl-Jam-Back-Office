package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;

import java.util.List;
import java.util.Map;

/**
 * Presenter interface for paginated survey units to close.
 * Extends SurveyUnitClosingPresenter to support pagination metadata.
 *
 * @param <T> the return type
 */
public interface PaginatedSurveyUnitClosingPresenter<T> extends SurveyUnitClosingPresenter<T> {

    /**
     * Presents the results with pagination information.
     *
     * @param projections the list of closable survey unit views
     * @param candidatesById map of candidate views by ID
     * @param questionnaireStates map of questionnaire states by survey unit ID
     * @param totalElements total number of eligible elements
     * @param pageNumber current page number (0-indexed)
     * @param pageSize number of elements per page
     * @return the presented result with pagination
     */
    T present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates,
            long totalElements,
            int pageNumber,
            int pageSize
    );
}
