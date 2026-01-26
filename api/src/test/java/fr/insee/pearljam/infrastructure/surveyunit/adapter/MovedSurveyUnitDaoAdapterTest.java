package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.PersonDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class MovedSurveyUnitDaoAdapterTest {

    @Autowired
    private MovedSurveyUnitDaoAdapter movedSurveyUnitDaoAdapter;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    private final String surveyUnitId = "SU-MOVED-TEST";

    @BeforeEach
    void setup() {
        // Create a survey unit with multiple persons
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setId(surveyUnitId);
        surveyUnit.setPriority(false);

        // Add some persons
        PersonDB person1 = new PersonDB();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setEmail("john.doe@example.com");
        person1.setSurveyUnit(surveyUnit);

        PersonDB person2 = new PersonDB();
        person2.setFirstName("Jane");
        person2.setLastName("Smith");
        person2.setEmail("jane.smith@example.com");
        person2.setSurveyUnit(surveyUnit);

        Set<PersonDB> persons = new HashSet<>();
        persons.add(person1);
        persons.add(person2);
        surveyUnit.setPersons(persons);

        // Add some initial states
        State initialState = new State(System.currentTimeMillis() - 10000, surveyUnit, StateType.VIN);
        surveyUnit.getStates().add(initialState);

        surveyUnitRepository.save(surveyUnit);
    }

    @Test
    @DisplayName("Should update survey unit with moved status")
    void shouldUpdateMovedSurveyUnit() {
        // When
        movedSurveyUnitDaoAdapter.updateMovedSurveyUnit(surveyUnitId);

        // Then
        Optional<SurveyUnit> surveyUnitOptional = surveyUnitRepository.findById(surveyUnitId);
        assertThat(surveyUnitOptional).isPresent();
        SurveyUnit surveyUnit = surveyUnitOptional.get();

        // Verify priority is set to true
        assertThat(surveyUnit.isPriority()).isTrue();

        // Verify states are cleared and new INS state is added
        assertThat(surveyUnit.getStates()).hasSize(1);
        State state = surveyUnit.getStates().iterator().next();
        assertThat(state.getType()).isEqualTo(StateType.INS);

        // Verify identification demenagementWeb is set to true
        if (surveyUnit.getIdentification() != null) {
            assertThat(surveyUnit.getIdentification().getDemenagementWeb()).isTrue();
        }

        // Verify only one person exists with firstName="PRENOM" and lastName="NOM"
        assertThat(surveyUnit.getPersons()).hasSize(1);
        PersonDB person = surveyUnit.getPersons().iterator().next();
        assertThat(person.getFirstName()).isEqualTo("PRENOM");
        assertThat(person.getLastName()).isEqualTo("NOM");
        assertThat(person.getEmail()).isNull();
    }

    @Test
    @DisplayName("Should handle survey unit with no persons")
    void shouldHandleSurveyUnitWithNoPersons() {
        // Given: Create a survey unit with no persons
        String emptySurveyUnitId = "SU-EMPTY-TEST";
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setId(emptySurveyUnitId);
        surveyUnit.setPriority(false);
        surveyUnit.setPersons(new HashSet<>());
        surveyUnitRepository.save(surveyUnit);

        // When
        movedSurveyUnitDaoAdapter.updateMovedSurveyUnit(emptySurveyUnitId);

        // Then
        Optional<SurveyUnit> surveyUnitOptional = surveyUnitRepository.findById(emptySurveyUnitId);
        assertThat(surveyUnitOptional).isPresent();
        SurveyUnit updatedSurveyUnit = surveyUnitOptional.get();

        // Verify a new person was created
        assertThat(updatedSurveyUnit.getPersons()).hasSize(1);
        PersonDB person = updatedSurveyUnit.getPersons().iterator().next();
        assertThat(person.getFirstName()).isEqualTo("PRENOM");
        assertThat(person.getLastName()).isEqualTo("NOM");
    }

    @Test
    @DisplayName("Should do nothing when survey unit does not exist")
    void shouldDoNothingWhenSurveyUnitDoesNotExist() {
        // When
        String nonExistentId = "NON-EXISTENT-ID";
        movedSurveyUnitDaoAdapter.updateMovedSurveyUnit(nonExistentId);

        // Then
        Optional<SurveyUnit> surveyUnitOptional = surveyUnitRepository.findById(nonExistentId);
        assertThat(surveyUnitOptional).isEmpty();
    }
}
