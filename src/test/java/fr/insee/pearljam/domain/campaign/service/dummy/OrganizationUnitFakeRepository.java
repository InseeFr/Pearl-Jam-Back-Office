package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.count.model.OrganizationUnitLabel;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnit;
import fr.insee.pearljam.domain.organizationunit.port.serverside.OrganizationUnitRepository;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizationUnitFakeRepository implements OrganizationUnitRepository {

    @Setter
    private List<OrganizationUnit> organizationUnits = new ArrayList<>();

    @Override
    public Optional<OrganizationUnit> findById(String id) {
        return organizationUnits.stream().filter(organizationUnit -> organizationUnit.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<OrganizationUnit> findByIdIgnoreCase(String id) {
        return organizationUnits.stream().filter(organizationUnit -> organizationUnit.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    @Override
    public Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(String id) {
        return findByIdIgnoreCase(id).map(ou -> new OrganizationUnitDto(ou.getId(), ou.getLabel()));
    }

    @Override
    public List<String> findChildrenId(String orgUnitId) {
        return List.of();
    }

    @Override
    public List<OrganizationUnit> findChildren(String orgUnitId) {
        return List.of();
    }

    @Override
    public List<String> findAllId() {
        return organizationUnits.stream().map(OrganizationUnit::getId).toList();
    }

    @Override
    public String findLabel(String orgUnitId) {
        return findById(orgUnitId).map(OrganizationUnit::getLabel).orElse("");
    }

    @Override
    public List<OrganizationUnit> findSubtree(String rootId) {
        return List.of();
    }

    @Override
    public List<OrganizationUnitLabel> findLabelsByIds(List<String> ids) {
        return List.of();
    }

    @Override
    public OrganizationUnit save(OrganizationUnit organizationUnit) {
        findById(organizationUnit.getId()).ifPresent(organizationUnits::remove);
        organizationUnits.add(organizationUnit);
        return organizationUnit;
    }

    @Override
    public List<OrganizationUnit> findAll() {
        return new ArrayList<>(organizationUnits);
    }

    @Override
    public List<OrganizationUnit> findAllById(Iterable<String> ids) {
        List<OrganizationUnit> result = new ArrayList<>();
        for (String id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public void delete(OrganizationUnit organizationUnit) {
        organizationUnits.remove(organizationUnit);
    }
}
