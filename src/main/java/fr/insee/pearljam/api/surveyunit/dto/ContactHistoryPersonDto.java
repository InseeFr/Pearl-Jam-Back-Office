package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;


public record ContactHistoryPersonDto(
        Long id,
        @NotNull
        Title title,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        Long birthdate,
        boolean panel
) {

    public static Person toModel(ContactHistoryPersonDto chPerson, ContactHistory contactHistory) {
        return new Person(chPerson.id(), chPerson.title(), chPerson.firstName(), chPerson.lastName(), null, chPerson.birthdate(), false, chPerson.panel(), new HashSet<>(), contactHistory);
    }

    public static ContactHistoryPersonDto fromModel(Person person) {
        return new ContactHistoryPersonDto(person.id(), person.title(), person.firstName(), person.lastName(), person.birthdate(), person.isPanel());
    }
}
