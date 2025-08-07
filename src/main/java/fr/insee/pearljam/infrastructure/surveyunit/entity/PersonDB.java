package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import fr.insee.pearljam.domain.surveyunit.model.person.PhoneNumber;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "person")
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDB implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Title title;
    private String firstName;
    private String lastName;
    private String email;
    private Long birthdate;
    private boolean privileged;

    @ManyToOne(fetch = FetchType.LAZY)
    private SurveyUnit surveyUnit;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = PhoneNumber.class, cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private Set<PhoneNumberDB> phoneNumbers = new HashSet<>();

    private boolean panel;

    @ManyToOne(fetch = FetchType.LAZY)
    private ContactHistoryDB contactHistory;

    public static PersonDB fromModel(Person person, SurveyUnit surveyUnit) {

        PersonDB personDB = new PersonDB(
                person.id(),
                person.title(),
                person.firstName(),
                person.lastName(),
                person.email(),
                person.birthdate(),
                person.privileged(),
                surveyUnit,
                null,
                person.isPanel(),
                ContactHistoryDB.fromModel(person.contactHistory(), surveyUnit));
        personDB.setPhoneNumbers(new HashSet<>(PhoneNumberDB.fromModel(person.phoneNumbers().stream().toList(), personDB)));

        return personDB;
    }

    public static Person toModel(PersonDB person) {
        return new Person(person.getId(), person.getTitle(), person.getFirstName(), person.getLastName(),
                person.getEmail(), person.getBirthdate(), person.isPrivileged(), person.isPanel(),
                PhoneNumberDB.toModel(person.getPhoneNumbers()), ContactHistoryDB.toModel(person.getContactHistory()));
    }

}
