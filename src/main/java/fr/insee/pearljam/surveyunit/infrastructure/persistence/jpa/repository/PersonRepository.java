package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository;

import java.util.Optional;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public interface PersonRepository extends JpaRepository<Person, Long> {
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
	Optional<Person> findById(Long id);
	
	
}
