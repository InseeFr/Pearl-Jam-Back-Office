package fr.insee.pearljam.api.surveyunit.dto.contactHistory;

import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import jakarta.validation.constraints.NotNull;


public record PreviousContactHistoryPersonDto(
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

    public static Person toModel(PreviousContactHistoryPersonDto chPerson, ContactHistory contactHistory) {
        return new Person(chPerson.id(), chPerson.title(), chPerson.firstName(), chPerson.lastName(), null, chPerson.birthdate(), false, chPerson.panel(), null, contactHistory);
    }

    public static PreviousContactHistoryPersonDto fromModel(Person person) {
        return new PreviousContactHistoryPersonDto(person.id(), person.title(), person.firstName(), person.lastName(), person.birthdate(), person.isPanel());
    }
}
