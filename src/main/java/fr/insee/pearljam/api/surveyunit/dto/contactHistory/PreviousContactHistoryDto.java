package fr.insee.pearljam.api.surveyunit.dto.contactHistory;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistoryType;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record PreviousContactHistoryDto(
        String comment,
        @NotNull
        ContactOutcomeType contactOutcomeValue,
        List<@Valid PreviousContactHistoryPersonDto> persons
) {

    public static PreviousContactHistoryDto fromModel(ContactHistory contactHistory) {
        if (contactHistory == null) {
            return null;
        }
        return new PreviousContactHistoryDto(contactHistory.comment(), contactHistory.contactOutcomeValue(), contactHistory.persons().stream().map(PreviousContactHistoryPersonDto::fromModel).toList());
    }

    public static ContactHistory toModel(PreviousContactHistoryDto contactHistoryDto) {
        if (contactHistoryDto==null){
            return null;
        }
        ContactHistory contactHistory = new ContactHistory(
                ContactHistoryType.PREVIOUS,
                contactHistoryDto.comment(),
                contactHistoryDto.contactOutcomeValue(),
                new HashSet<>()
        );
        Set<Person> chPersons = contactHistoryDto.persons().stream()
                .map(person -> PreviousContactHistoryPersonDto.toModel(person, contactHistory)).collect(Collectors.toSet());
        contactHistory.persons().addAll(chPersons);

        return contactHistory;

    }
}
