package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.dto.address.AddressDto;

public interface AddressRepository extends JpaRepository<InseeAddress, Long> {
	@Query("SELECT new fr.insee.pearljam.api.dto.address.AddressDto(a.l1,a.l2,a.l3,a.l4,a.l5,a.l6,a.l7) FROM Address a WHERE a.id=?1")
	AddressDto findDtoById(Long id);
}
