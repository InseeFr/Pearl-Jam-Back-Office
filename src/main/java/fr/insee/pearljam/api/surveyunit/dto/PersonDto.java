package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.api.dto.phonenumber.PhoneNumberDto;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public record PersonDto(
        Long id,
        @NotNull
        Title title,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        String email,
        Long birthdate,
        boolean privileged,
        boolean panel,
        List<PhoneNumberDto> phoneNumbers
) {


    public static Person toModel(PersonDto person, ContactHistory contactHistory) {
        return new Person(person.id(),
                person.title(),
                person.firstName(),
                person.lastName(),
                person.email(),
                person.birthdate(),
                person.privileged(),
                person.panel(),
                person.phoneNumbers().stream().map(PhoneNumberDto::toModel).collect(Collectors.toSet()),
                contactHistory);
    }

    public static PersonDto fromModel(Person person) {
        return new PersonDto(person.id(), person.title(), person.firstName(), person.lastName(), person.email(), person.birthdate(), person.privileged(), person.isPanel(), person.phoneNumbers().stream().map(PhoneNumberDto::fromModel).toList());
    }
}
