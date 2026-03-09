package fr.insee.pearljam.domain.organizationunit.port.serverside;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.count.model.OrganizationUnitLabel;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnit;

import java.util.List;
import java.util.Optional;

public interface OrganizationUnitRepository {
    Optional<OrganizationUnit> findById(String id);

    Optional<OrganizationUnit> findByIdIgnoreCase(String id);

    boolean existsById(String id);

    Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(String id);

    List<String> findChildrenId(String orgUnitId);

    List<OrganizationUnit> findChildren(String orgUnitId);

    List<String> findAllId();

    String findLabel(String orgUnitId);

    List<OrganizationUnit> findSubtree(String rootId);

    List<OrganizationUnitLabel> findLabelsByIds(List<String> ids);

    OrganizationUnit save(OrganizationUnit organizationUnit);

    List<OrganizationUnit> findAll();

    List<OrganizationUnit> findAllById(Iterable<String> ids);

    void delete(OrganizationUnit organizationUnit);
}
