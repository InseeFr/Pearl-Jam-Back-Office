package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistoryType;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record ContactHistoryDto(
        String comment,
        ContactOutcomeType contactOutcomeValue,
        ContactHistoryType contactHistoryType,
        List<ContactHistoryPersonDto> persons
) {

    public static ContactHistoryDto fromModel(@NotNull ContactHistory contactHistory) {
        return new ContactHistoryDto(contactHistory.comment(), contactHistory.contactOutcomeValue(), contactHistory.historyType(), contactHistory.persons().stream().map(ContactHistoryPersonDto::fromModel).toList());
    }

    public static ContactHistory toModel(@NotNull ContactHistoryDto contactHistoryDto) {

        ContactHistory contactHistory = new ContactHistory(
                contactHistoryDto.contactHistoryType(),
                contactHistoryDto.comment(),
                contactHistoryDto.contactOutcomeValue(),
                new HashSet<>()
        );
        Set<Person> chPersons = contactHistoryDto.persons().stream()
                .map(person -> ContactHistoryPersonDto.toModel(person, contactHistory)).collect(Collectors.toSet());
        contactHistory.persons().addAll(chPersons);

        return contactHistory;

    }
}
