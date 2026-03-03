package fr.insee.pearljam.domain.surveyunit.model.contacthistory;

import fr.insee.pearljam.api.domain.Source;
import fr.insee.pearljam.api.domain.Title;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private ContactHistory contactHistory(String comment) {
        return new ContactHistory(ContactHistoryType.PREVIOUS,
                comment,
                HistoryContactOutcomeType.IMP,
                new HashSet<>()
        );
    }

    private final ContactHistory niceCH = contactHistory("nice comment");
    private final ContactHistory otherNiceCH = contactHistory("other nice comment");

    private Person base(ContactHistory ch) {
        Person person = new Person(1L,
                Title.MISTER,
                "John",
                "Doe",
                "john@doe.fr",
                123456789L,
                true,
                Boolean.TRUE,
                Set.of(new PhoneNumber(Source.INTERVIEWER, true, "0600000000")),
                ch
        );
        ch.persons().add(person);
        return person;
    }

    @Test
    void equals_sameInstance_true() {
        Person p = base(niceCH);
        assertEquals(p, p);
    }

    @Test
    void equals_null_false() {
        Person p = base(niceCH);
        assertNotEquals(null, p);
    }

    @Test
    void equals_otherType_false() {
        Person p = base(niceCH);
        assertNotEquals("not a person", p);
    }

    @Test
    void equals_sameFields_true_evenIfContactHistoryDiffers() {
        Person p1 = base(niceCH);
        Person p2 = base(otherNiceCH);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode(), "hashCode must match when equals is true");
    }

    @Test
    void equals_oneFieldDifferent_false() {
        Person p1 = base(niceCH);
        Person p2 = new Person(2L, // id differs
                Title.MISTER,
                "John",
                "Doe",
                "john@doe.fr",
                123456789L,
                true,
                Boolean.TRUE,
                Set.of(new PhoneNumber(Source.INTERVIEWER, true, "0600000000")), otherNiceCH);

        assertNotEquals(p1, p2);
    }
}

