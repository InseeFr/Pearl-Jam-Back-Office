package fr.insee.pearljam.infrastructure.surveyunit.jpa;

import fr.insee.pearljam.infrastructure.surveyunit.entity.PersonDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface PersonJpaRepository extends JpaRepository<PersonDB, Long> {
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
	Optional<PersonDB> findById(Long id);
	
	
}
