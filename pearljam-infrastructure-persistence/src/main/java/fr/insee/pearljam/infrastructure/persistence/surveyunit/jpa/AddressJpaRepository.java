package fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InseeAddressDB;
import org.springframework.data.jpa.repository.JpaRepository;

/**
* AddressRepository is the repository using to access to  Address table in DB
* 
* @author scorcaud
* 
*/
public interface AddressJpaRepository extends JpaRepository<InseeAddressDB, Long> {
	
}
