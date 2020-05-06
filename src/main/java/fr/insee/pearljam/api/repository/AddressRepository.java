package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.dto.address.AddressDto;

public interface AddressRepository extends JpaRepository<InseeAddress, Long> {
	AddressDto findDtoById(Long id);
}
