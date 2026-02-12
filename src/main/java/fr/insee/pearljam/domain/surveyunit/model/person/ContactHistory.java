package fr.insee.pearljam.domain.surveyunit.model.person;

import fr.insee.pearljam.api.domain.ContactOutcomeType;

import java.util.Set;

public record ContactHistory(
        ContactHistoryType historyType,
        String comment,
        ContactOutcomeType contactOutcomeValue,
        Set<Person>persons) {
}
