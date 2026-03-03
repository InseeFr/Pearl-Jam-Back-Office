package fr.insee.pearljam.api.surveyunit.dto.contactHistory;

import fr.insee.pearljam.domain.surveyunit.model.contacthistory.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.contacthistory.ContactHistoryType;
import fr.insee.pearljam.domain.surveyunit.model.contacthistory.Person;
import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record NextContactHistoryDto(
        List<@Valid NextContactHistoryPersonDto> persons
) {

    public static NextContactHistoryDto fromModel(ContactHistory contactHistory) {
        if (contactHistory == null) {
            return null;
        }
        return new NextContactHistoryDto(contactHistory.persons().stream().map(NextContactHistoryPersonDto::fromModel).toList());
    }

    public static ContactHistory toModel(NextContactHistoryDto contactHistoryDto) {
        if (contactHistoryDto == null) {
            return null;
        }
        ContactHistory contactHistory = new ContactHistory(
                ContactHistoryType.NEXT,
                null,
                null,
                new HashSet<>()
        );
        Set<Person> chPersons = contactHistoryDto.persons().stream()
                .map(person -> NextContactHistoryPersonDto.toModel(person, contactHistory)).collect(Collectors.toSet());
        contactHistory.persons().addAll(chPersons);

        return contactHistory;

    }
}
