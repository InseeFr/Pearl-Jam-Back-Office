package fr.insee.pearljam.domain.surveyunit.port.serverside;

import fr.insee.pearljam.domain.surveyunit.model.InseeAddress;

import java.util.Optional;

public interface AddressRepository {
    Optional<InseeAddress> findById(Long id);

    InseeAddress save(InseeAddress inseeAddress);
}
