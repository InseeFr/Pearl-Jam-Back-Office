package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistoryType;

public record ContactHistoryDto(
        String comment,
        ContactOutcomeType contactOutcomeValue,
        ContactHistoryType contactHistoryType
) {

    public static ContactHistoryDto fromModel(ContactHistory contactHistory) {
        return new ContactHistoryDto(contactHistory.comment(), contactHistory.contactOutcomeValue(), contactHistory.historyType());
    }

    public static ContactHistory toModel(ContactHistoryDto contactHistoryDto, String surveyUnitId) {
        return new ContactHistory(contactHistoryDto.contactHistoryType(), contactHistoryDto.comment(), contactHistoryDto.contactOutcomeValue(), surveyUnitId);
    }
}
