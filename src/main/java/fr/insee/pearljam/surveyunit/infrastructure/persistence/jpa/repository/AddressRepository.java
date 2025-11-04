package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.InseeAddress;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.AddressDto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
* AddressRepository is the repository using to access to  Address table in DB
* 
* @author scorcaud
* 
*/
public interface AddressRepository extends JpaRepository<InseeAddress, Long> {
	
	AddressDto findDtoById(Long id);

}
