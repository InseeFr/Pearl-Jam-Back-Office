package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.InseeSampleIdentifier;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;

/**
* SampleIdentifierRepository is the repository using to access to InseeSampleIdentifier table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface SampleIdentifierRepository extends JpaRepository<InseeSampleIdentifier, Long> {
	
	/**
	 * This method retrieve the SampleIdentifiers in db by Id
	 * @param id
	 * @return SampleIdentifiersDto
	 */
	SampleIdentifiersDto findDtoById(Long id);
}
