package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.InseeSampleIdentifier;
import fr.insee.pearljam.api.dto.sampleidentifier.SampleIdentifiersDto;

public interface SampleIdentifierRepository extends JpaRepository<InseeSampleIdentifier, Long> {
	SampleIdentifiersDto findDtoById(Long id);
}
