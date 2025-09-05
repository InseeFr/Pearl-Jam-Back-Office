package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.Source;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistoryType;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_unit_id", nullable = false) // this binding can write the column
    private SurveyUnit surveyUnit;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = PhoneNumberDB.class,
            cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private Set<PhoneNumberDB> phoneNumbers = new HashSet<>();

    private boolean panel;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_history_type", length = 10) // this binding can write the column
    private ContactHistoryType contactHistoryType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_unit_id", referencedColumnName = "survey_unit_id",
            insertable = false, updatable = false)// won't be null due to surveyUnit binding
    @JoinColumn(name = "contact_history_type", referencedColumnName = "contact_history_type",
            insertable = false, updatable = false)
    private ContactHistoryDB contactHistory;

    public static PersonDB fromModel(Person person, ContactHistoryDB contactHistoryDb, SurveyUnit surveyUnit) {

        ContactHistoryType contactHistoryType = contactHistoryDb != null
                ? contactHistoryDb.getId().getContactHistoryType()
                : null;

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
                contactHistoryType,
                contactHistoryDb);
        personDB.setPhoneNumbers(new HashSet<>(PhoneNumberDB.fromModel(person.phoneNumbers().stream().toList(), personDB)));

        return personDB;
    }

    public static Person toModel(PersonDB person, ContactHistory contactHistory) {

        return new Person(person.getId(),
                person.getTitle(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getBirthdate(),
                person.isPrivileged(),
                person.isPanel(),
                PhoneNumberDB.toModel(person.getPhoneNumbers()),
                contactHistory);
    }

    public void updateFrom(PersonDB next) {
        setTitle(next.getTitle());
        setFirstName(next.getFirstName());
        setLastName(next.getLastName());
        setEmail(next.getEmail());
        setBirthdate(next.getBirthdate());
        setPrivileged(next.isPrivileged());
        setPanel(next.isPanel());

        String nextPhoneNumber = next.getPhoneNumbers().stream().findFirst().map(PhoneNumberDB::getNumber).orElse(null);
        updateContactHistoryPhoneNumbers(nextPhoneNumber);
    }


    private void updateContactHistoryPhoneNumbers(String nextPhoneNumber) {
        Set<PhoneNumberDB> existing = this.getPhoneNumbers();
        PhoneNumberDB phone = existing.stream().findFirst().orElse(null);

        if (nextPhoneNumber == null || nextPhoneNumber.isBlank()) {
            existing.clear();                     // remove phone
            return;
        }
        if (phone == null) {
            phone = new PhoneNumberDB();
            phone.setPerson(this);                // owning side!
            existing.add(phone);
        }
        phone.setNumber(nextPhoneNumber);
        phone.setFavorite(false);                  // or whatever default
        phone.setSource(Source.INTERVIEWER);
        phone.setPerson(this);

    }

}
