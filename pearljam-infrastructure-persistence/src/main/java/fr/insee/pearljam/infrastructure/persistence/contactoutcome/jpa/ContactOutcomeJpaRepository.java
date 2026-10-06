package fr.insee.pearljam.infrastructure.persistence.contactoutcome.jpa;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.ContactOutcomeDB;
import org.springframework.data.jpa.repository.JpaRepository;

/**
* ContactOutcomeRepository is the repository using to access to ContactOutcome table in DB
* 
* @author scorcaud
* 
*/
public interface ContactOutcomeJpaRepository extends JpaRepository<ContactOutcomeDB, Long> {

}
