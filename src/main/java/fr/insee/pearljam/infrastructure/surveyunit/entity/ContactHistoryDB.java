package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.person.ContactHistory;
import fr.insee.pearljam.domain.surveyunit.model.person.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity(name = "contact_history")
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactHistoryDB implements Serializable {

    @Serial
    private static final long serialVersionUID = 6363481673399032153L;

    @EmbeddedId
    private ContactHistoryPK id;  // The composite key

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_unit_id", nullable = false)
    @MapsId("surveyUnitId")
    private SurveyUnit surveyUnit;

    @Column(length = 999)
    private String comment;

    @Enumerated(EnumType.STRING)
    private ContactOutcomeType contactOutcomeValue;

    @OneToMany(mappedBy = "contactHistory", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PersonDB> persons = new HashSet<>();

    public static ContactHistoryDB fromModel(ContactHistory contactHistory, SurveyUnit surveyUnit) {
        if (contactHistory == null) {
            return null;
        }

        ContactHistoryDB contactHistoryDB = new ContactHistoryDB(new ContactHistoryPK(surveyUnit.getId(), contactHistory.historyType()),
                surveyUnit,
                contactHistory.comment(),
                contactHistory.contactOutcomeValue(),
                new HashSet<>());

        contactHistory.persons().stream()
                .map(person -> PersonDB.fromModel(person, contactHistoryDB, surveyUnit))
                .forEach(person -> contactHistoryDB.getPersons().add(person));

        return contactHistoryDB;
    }

    public static ContactHistory toModel(ContactHistoryDB contactHistoryDB) {
        if (contactHistoryDB == null) {
            return null;
        }
        ContactHistory contactHistory = new ContactHistory(contactHistoryDB.getId().getContactHistoryType(),
                contactHistoryDB.getComment(),
                contactHistoryDB.getContactOutcomeValue(),
                new HashSet<>());
        contactHistoryDB.getPersons().stream()
                .map(person -> PersonDB.toModel(person, contactHistory))
                .forEach(person -> contactHistory.persons().add(person));

        return contactHistory;
    }

    public void updateFrom(ContactHistoryDB next){
        setComment(next.getComment());
        setContactOutcomeValue(next.getContactOutcomeValue());
        updatePersons( next.getPersons());

    }

    private void updatePersons(Set<PersonDB> nextPersons){
        if (nextPersons == null) nextPersons = Collections.emptySet();

        // Index existing persons by DB id
        Map<Long, PersonDB> existingById = this.persons.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(PersonDB::getId, Function.identity()));

        // Incoming ids (those with id!=null)
        Set<Long> incomingIds = nextPersons.stream()
                .map(PersonDB::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        Set<PersonDB> survivors = new HashSet<>();
        // remove existing persons missing from incoming
        this.persons.removeIf(p -> !incomingIds.contains(p.getId()));

        for (PersonDB inc : nextPersons) {
            PersonDB managed = (inc.getId() != null) ? existingById.get(inc.getId()) : null;

            // create new persons
            if (managed == null) {
                Person modelPerson = PersonDB.toModel(inc, ContactHistoryDB.toModel(this));
                PersonDB created = PersonDB.fromModel(modelPerson, this, this.getSurveyUnit());
                this.persons.add(created);
                survivors.add(created);
            } else {
                // update existing persons
                managed.updateFrom(inc);
                survivors.add(managed);
            }
        }

        this.persons.removeIf(p -> !survivors.contains(p));


    }

}
