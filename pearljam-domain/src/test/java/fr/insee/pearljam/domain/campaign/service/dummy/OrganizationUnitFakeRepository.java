package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.surveyunit.model.count.OrganizationUnitLabel;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizationUnitFakeRepository implements OrganizationUnitRepository {

    @Setter
    private List<OrganizationUnitDB> organizationUnits = new ArrayList<>();

    @Override
    public Optional<OrganizationUnitDB> findById(String id) {
        return organizationUnits.stream().filter(organizationUnit -> organizationUnit.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<OrganizationUnitDB> findByIdIgnoreCase(String id) {
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
    public List<OrganizationUnitDB> findChildren(String orgUnitId) {
        return List.of();
    }

    @Override
    public List<String> findAllId() {
        return organizationUnits.stream().map(OrganizationUnitDB::getId).toList();
    }

    @Override
    public String findLabel(String orgUnitId) {
        return findById(orgUnitId).map(OrganizationUnitDB::getLabel).orElse("");
    }

    @Override
    public List<OrganizationUnitDB> findSubtree(String rootId) {
        return List.of();
    }

    @Override
    public List<OrganizationUnitLabel> findLabelsByIds(List<String> ids) {
        return List.of();
    }

    @Override
    public OrganizationUnitDB save(OrganizationUnitDB organizationUnit) {
        findById(organizationUnit.getId()).ifPresent(organizationUnits::remove);
        organizationUnits.add(organizationUnit);
        return organizationUnit;
    }

    @Override
    public List<OrganizationUnitDB> findAll() {
        return new ArrayList<>(organizationUnits);
    }

    @Override
    public List<OrganizationUnitDB> findAllById(Iterable<String> ids) {
        List<OrganizationUnitDB> result = new ArrayList<>();
        for (String id : ids) {
            findById(id).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public void delete(OrganizationUnitDB organizationUnit) {
        organizationUnits.remove(organizationUnit);
    }
}
