package fr.insee.pearljam.infrastructure.organizationunit.adapter;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.count.model.OrganizationUnitLabel;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnit;
import fr.insee.pearljam.domain.organizationunit.port.serverside.OrganizationUnitRepository;
import fr.insee.pearljam.infrastructure.organizationunit.jpa.OrganizationUnitJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrganizationUnitDaoAdapter implements OrganizationUnitRepository {
    private final OrganizationUnitJpaRepository organizationUnitJpaRepository;

    @Override
    public Optional<OrganizationUnit> findById(String id) {
        return organizationUnitJpaRepository.findById(id);
    }

    @Override
    public Optional<OrganizationUnit> findByIdIgnoreCase(String id) {
        return organizationUnitJpaRepository.findByIdIgnoreCase(id);
    }

    @Override
    public boolean existsById(String id) {
        return organizationUnitJpaRepository.existsById(id);
    }

    @Override
    public Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(String id) {
        return organizationUnitJpaRepository.findDtoByIdIgnoreCase(id);
    }

    @Override
    public List<String> findChildrenId(String orgUnitId) {
        return organizationUnitJpaRepository.findChildrenId(orgUnitId);
    }

    @Override
    public List<OrganizationUnit> findChildren(String orgUnitId) {
        return organizationUnitJpaRepository.findChildren(orgUnitId);
    }

    @Override
    public List<String> findAllId() {
        return organizationUnitJpaRepository.findAllId();
    }

    @Override
    public String findLabel(String orgUnitId) {
        return organizationUnitJpaRepository.findLabel(orgUnitId);
    }

    @Override
    public List<OrganizationUnit> findSubtree(String rootId) {
        return organizationUnitJpaRepository.findSubtree(rootId);
    }

    @Override
    public List<OrganizationUnitLabel> findLabelsByIds(List<String> ids) {
        return organizationUnitJpaRepository.findLabelsByIds(ids);
    }

    @Override
    public OrganizationUnit save(OrganizationUnit organizationUnit) {
        return organizationUnitJpaRepository.save(organizationUnit);
    }

    @Override
    public List<OrganizationUnit> findAll() {
        return organizationUnitJpaRepository.findAll();
    }

    @Override
    public List<OrganizationUnit> findAllById(Iterable<String> ids) {
        return organizationUnitJpaRepository.findAllById(ids);
    }

    @Override
    public void delete(OrganizationUnit organizationUnit) {
        organizationUnitJpaRepository.delete(organizationUnit);
    }
}
