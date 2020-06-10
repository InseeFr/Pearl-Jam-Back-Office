package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.dto.address.AddressDto;

/**
* AddressRepository is the repository using to access to  Comment table in DB
* 
* @author scorcaud
* 
*/
public interface AddressRepository extends JpaRepository<InseeAddress, Long> {
	
	/**
	* This method retrieve Comment in DB by an Id
	* @return List of all {@link AddressDto}
	*/
	AddressDto findDtoById(Long id);
}
