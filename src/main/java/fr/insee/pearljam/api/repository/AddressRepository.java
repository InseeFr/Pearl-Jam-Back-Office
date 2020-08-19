package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.dto.address.AddressDto;

/**
* AddressRepository is the repository using to access to  Address table in DB
* 
* @author scorcaud
* 
*/
public interface AddressRepository extends JpaRepository<InseeAddress, Long> {
	
	AddressDto findDtoById(Long id);

	@Query(value="SELECT l6 from address add "
			+ "INNER JOIN survey_unit su ON su.address_id = add.id "
			+ "WHERE su.id = ?1", nativeQuery=true)
	String findLocationAndCityBySurveyUnitId(String idSurveyUnit);
}
