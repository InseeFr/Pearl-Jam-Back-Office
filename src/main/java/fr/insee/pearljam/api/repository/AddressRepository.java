package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
	@Query("SELECT new fr.insee.pearljam.api.dto.address.AddressDto(a.l1,a.l2,a.l3,a.l4,a.l5,a.l6,a.l7) FROM Address a WHERE a.id=?1")
	AddressDto findDtoById(Long id);
}
