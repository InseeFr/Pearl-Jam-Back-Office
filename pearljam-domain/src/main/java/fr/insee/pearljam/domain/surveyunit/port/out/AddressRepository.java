package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InseeAddressDB;

import java.util.Optional;

public interface AddressRepository {
    Optional<InseeAddressDB> findById(Long id);

    InseeAddressDB save(InseeAddressDB inseeAddress);
}
