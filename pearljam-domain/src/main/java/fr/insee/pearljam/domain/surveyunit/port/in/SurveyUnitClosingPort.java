package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotClosableException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import jakarta.annotation.Nullable;

import java.util.List;

public interface SurveyUnitClosingPort {
    /**
     * Adds a closing cause to multiple survey units, either as a temporary or definitive cause.
     *
     * <p><b>Behavior:</b>
     * <ul>
     *   <li>If {@code toClose} is {@code false}: provisional closing
     *       <ul>
     *         <li>Allows updating an existing temporary closing cause (no uniqueness validation).
     *         <li>Validates that all survey units are in closable states (not CLO/TBR/FIN).
     *         <li>Does NOT close the survey units (state remains unchanged).
     *       </ul>
     *   <li>If {@code toClose} is {@code true}: definitive closing
     *       <ul>
     *         <li>Updates any existing provisional closing causes to the new type.
     *         <li>Validates that all survey units are in closable states (not CLO/TBR/FIN).
     *         <li>Closes the survey units by setting their state to {@link StateType#CLO}.
     *       </ul>
     * </ul>
     *
     * @param surveyUnitIds the IDs of the survey units to update. Must not be null or empty.
     * @param type          the type of closing cause to apply.
     * @param toClose   if {@code false }, the closing cause is temporary and can be modified later;
     *                      if {@code true}, the closing cause is definitive and the survey units will be closed.
     * @throws SurveyUnitNotFoundException      if any survey unit ID does not exist.
     * @throws SurveyUnitNotClosableException    if any survey unit is in a non-closable state (CLO, TBR, or FIN).
     */
    void addClosingCauseToMultipleSurveyUnits(List<String> surveyUnitIds, ClosingCauseType type, boolean toClose);

    <T> T getSurveyUnitsToClose(String userId, @Nullable String campaignId, SurveyUnitClosingPresenter<T> presenter );

    default <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter) {
        return getSurveyUnitsToClose(userId, null, presenter);
    }

    void deleteClosingCauseBySurveyUnitId(String surveyUnitId);
}
