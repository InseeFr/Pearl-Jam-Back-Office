package fr.insee.pearljam.api.surveyunit.dto.contactHistory;

import fr.insee.pearljam.api.domain.Source;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import fr.insee.pearljam.domain.surveyunit.model.person.PhoneNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public record ContactHistoryPersonDto(
        Long id,
        @NotNull
        Title title,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @Pattern(regexp = "\\+?\\d+", message = "phone number must contain digits 0–9 only and can start with '+'")
        String phoneNumber,
        Long birthdate,
        boolean panel
) {

    public static Person toModel(ContactHistoryPersonDto chPerson, ContactHistory contactHistory) {
        Set<PhoneNumber> domainPhoneNumbers =
                Stream.of(chPerson.phoneNumber)
                        .filter(Objects::nonNull)
                        .map(phoneNumber -> new PhoneNumber(Source.INTERVIEWER, false, phoneNumber))
                        .collect(Collectors.toSet());
        return new Person(chPerson.id(), chPerson.title(), chPerson.firstName(), chPerson.lastName(), null, chPerson.birthdate(), false, chPerson.panel(), domainPhoneNumbers, contactHistory);
    }

    public static ContactHistoryPersonDto fromModel(Person person) {
        return new ContactHistoryPersonDto(person.id(), person.title(), person.firstName(), person.lastName(), person.phoneNumbers().stream().findFirst().map(PhoneNumber::number).orElse(null), person.birthdate(), person.isPanel());
    }
}
