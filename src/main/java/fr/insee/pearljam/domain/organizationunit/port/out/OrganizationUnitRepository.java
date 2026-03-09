package fr.insee.pearljam.domain.organizationunit.port.out;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.surveyunit.model.count.OrganizationUnitLabel;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;

import java.util.List;
import java.util.Optional;

public interface OrganizationUnitRepository {
    Optional<OrganizationUnitDB> findById(String id);

    Optional<OrganizationUnitDB> findByIdIgnoreCase(String id);

    boolean existsById(String id);

    Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(String id);

    List<String> findChildrenId(String orgUnitId);

    List<OrganizationUnitDB> findChildren(String orgUnitId);

    List<String> findAllId();

    String findLabel(String orgUnitId);

    List<OrganizationUnitDB> findSubtree(String rootId);

    List<OrganizationUnitLabel> findLabelsByIds(List<String> ids);

    OrganizationUnitDB save(OrganizationUnitDB organizationUnit);

    List<OrganizationUnitDB> findAll();

    List<OrganizationUnitDB> findAllById(Iterable<String> ids);

    void delete(OrganizationUnitDB organizationUnit);
}
