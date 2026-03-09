package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.domain.surveyunit.model.InseeAddress;
import fr.insee.pearljam.domain.surveyunit.port.serverside.AddressRepository;
import fr.insee.pearljam.infrastructure.surveyunit.jpa.AddressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AddressDaoAdapter implements AddressRepository {
    private final AddressJpaRepository addressJpaRepository;

    @Override
    public Optional<InseeAddress> findById(Long id) {
        return addressJpaRepository.findById(id);
    }

    @Override
    public InseeAddress save(InseeAddress inseeAddress) {
        return addressJpaRepository.save(inseeAddress);
    }
}
