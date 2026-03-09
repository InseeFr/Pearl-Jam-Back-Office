package fr.insee.pearljam.infrastructure.surveyunit.jpa;

import fr.insee.pearljam.domain.surveyunit.model.InseeAddress;
import org.springframework.data.jpa.repository.JpaRepository;

/**
* AddressRepository is the repository using to access to  Address table in DB
* 
* @author scorcaud
* 
*/
public interface AddressJpaRepository extends JpaRepository<InseeAddress, Long> {
	
}
