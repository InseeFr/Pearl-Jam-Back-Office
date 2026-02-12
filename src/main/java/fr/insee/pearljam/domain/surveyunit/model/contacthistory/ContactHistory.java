package fr.insee.pearljam.domain.surveyunit.model.contacthistory;

import java.util.Set;

public record ContactHistory(
        ContactHistoryType historyType,
        String comment,
        HistoryContactOutcomeType contactOutcomeValue,
        Set<Person>persons) {
}
