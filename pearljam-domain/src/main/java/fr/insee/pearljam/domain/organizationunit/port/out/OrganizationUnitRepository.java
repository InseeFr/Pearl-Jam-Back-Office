package fr.insee.pearljam.domain.organizationunit.port.out;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;

import java.util.List;
import java.util.Optional;

public interface OrganizationUnitRepository {
    Optional<OrganizationUnitDB> findById(String id);

    Optional<OrganizationUnitDB> findByIdIgnoreCase(String id);

    boolean existsById(String id);

    Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(String id);

    List<String> findChildrenId(String orgUnitId);

    List<String> findAllId();

    List<OrganizationUnitDB> findSubtree(String rootId);

    OrganizationUnitDB save(OrganizationUnitDB organizationUnit);

    List<OrganizationUnitDB> findAll();

    List<OrganizationUnitDB> findAllById(Iterable<String> ids);

    void delete(OrganizationUnitDB organizationUnit);
}
