package fr.insee.pearljam.api.surveyunit.dto.contactHistory;

import fr.insee.pearljam.api.domain.Source;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import fr.insee.pearljam.domain.surveyunit.model.person.PhoneNumber;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public record NextContactHistoryPersonDto(
        Long id,
        @NotNull
        Title title,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        String phoneNumber,
        String email,
        boolean preferredContact
) {

    public static Person toModel(NextContactHistoryPersonDto chPerson, ContactHistory contactHistory) {
        Set<PhoneNumber> domainPhoneNumbers =
                Stream.of(chPerson.phoneNumber())
                        .filter(Objects::nonNull)
                        .map(phoneNumber -> new PhoneNumber(Source.INTERVIEWER, false, phoneNumber))
                        .collect(Collectors.toSet());
        return new Person(chPerson.id(), chPerson.title(), chPerson.firstName(), chPerson.lastName(), chPerson.email(), null, chPerson.preferredContact(), false, domainPhoneNumbers, contactHistory);
    }

    public static NextContactHistoryPersonDto fromModel(Person person) {
        return new NextContactHistoryPersonDto(person.id(), person.title(), person.firstName(), person.lastName(), person.phoneNumbers().stream().findFirst().map(PhoneNumber::number).orElse(null), person.email(), person.privileged());
    }
}
