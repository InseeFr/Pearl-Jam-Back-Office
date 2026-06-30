package fr.insee.pearljam.domain.surveyunit.stub;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPresenter;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SurveyUnitClosingPortStub implements SurveyUnitClosingPort {

    private final Set<String> deletedClosingCauses = new HashSet<>();

    @Override
    public void addClosingCauseToMultipleSurveyUnits(List<String> surveyUnitId, ClosingCauseType type, boolean toClose) {
        // Not used by SurveyUnitStateService
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public <T> T getSurveyUnitsToClose(String userId, @Nullable String campaignId, SurveyUnitClosingPresenter<T> presenter) {
        return null;
    }

    @Override
    public <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter) {
        // Not used by SurveyUnitStateService
        throw new UnsupportedOperationException("Not implemented for unit tests");
    }

    @Override
    public void deleteClosingCauseBySurveyUnitId(String surveyUnitId) {
        deletedClosingCauses.add(surveyUnitId);
    }

    // Helper methods for tests
    public boolean wasClosingCauseDeletedFor(String surveyUnitId) {
        return deletedClosingCauses.contains(surveyUnitId);
    }

    public void reset() {
        deletedClosingCauses.clear();
    }
}
