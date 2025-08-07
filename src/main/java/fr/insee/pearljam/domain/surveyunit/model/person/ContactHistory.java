package fr.insee.pearljam.domain.surveyunit.model.person;

import fr.insee.pearljam.api.domain.ContactOutcomeType;

public record ContactHistory(
        ContactHistoryType historyType,
        String comment,
        ContactOutcomeType contactOutcomeValue,
        String surveyUnitId) {
}
