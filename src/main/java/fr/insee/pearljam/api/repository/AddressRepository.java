package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.api.domain.InseeAddress;
import org.springframework.data.jpa.repository.JpaRepository;

/**
* AddressRepository is the repository using to access to  Address table in DB
* 
* @author scorcaud
* 
*/
public interface AddressRepository extends JpaRepository<InseeAddress, Long> {
	
}
