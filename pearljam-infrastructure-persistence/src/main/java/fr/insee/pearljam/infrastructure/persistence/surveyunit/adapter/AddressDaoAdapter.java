package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InseeAddressDB;
import fr.insee.pearljam.domain.surveyunit.port.out.AddressRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.AddressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AddressDaoAdapter implements AddressRepository {
    private final AddressJpaRepository addressJpaRepository;

    @Override
    public Optional<InseeAddressDB> findById(Long id) {
        return addressJpaRepository.findById(id);
    }

    @Override
    public InseeAddressDB save(InseeAddressDB inseeAddress) {
        return addressJpaRepository.save(inseeAddress);
    }
}
