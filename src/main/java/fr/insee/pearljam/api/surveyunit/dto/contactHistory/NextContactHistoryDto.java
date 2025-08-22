package fr.insee.pearljam.api.surveyunit.dto.contactHistory;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistoryType;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record NextContactHistoryDto(
        String comment,
        ContactOutcomeType contactOutcomeValue,
        List<@Valid ContactHistoryPersonDto> persons
) {

    public static NextContactHistoryDto fromModel(ContactHistory contactHistory) {
        if (contactHistory == null) {
            return null;
        }
        return new NextContactHistoryDto(contactHistory.comment(), contactHistory.contactOutcomeValue(), contactHistory.persons().stream().map(ContactHistoryPersonDto::fromModel).toList());
    }

    public static ContactHistory toModel(NextContactHistoryDto contactHistoryDto) {
        if (contactHistoryDto == null) {
            return null;
        }
        ContactHistory contactHistory = new ContactHistory(
                ContactHistoryType.NEXT,
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
