package fr.insee.pearljam.infrastructure.persistence.organizationunit.adapter;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.surveyunit.model.count.OrganizationUnitLabel;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa.OrganizationUnitJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrganizationUnitDaoAdapter implements OrganizationUnitRepository {
    private final OrganizationUnitJpaRepository organizationUnitJpaRepository;

    @Override
    public Optional<OrganizationUnitDB> findById(String id) {
        return organizationUnitJpaRepository.findById(id);
    }

    @Override
    public Optional<OrganizationUnitDB> findByIdIgnoreCase(String id) {
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
    public List<OrganizationUnitDB> findChildren(String orgUnitId) {
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
    public List<OrganizationUnitDB> findSubtree(String rootId) {
        return organizationUnitJpaRepository.findSubtree(rootId);
    }

    @Override
    public List<OrganizationUnitLabel> findLabelsByIds(List<String> ids) {
        return organizationUnitJpaRepository.findLabelsByIds(ids);
    }

    @Override
    public OrganizationUnitDB save(OrganizationUnitDB organizationUnit) {
        return organizationUnitJpaRepository.save(organizationUnit);
    }

    @Override
    public List<OrganizationUnitDB> findAll() {
        return organizationUnitJpaRepository.findAll();
    }

    @Override
    public List<OrganizationUnitDB> findAllById(Iterable<String> ids) {
        return organizationUnitJpaRepository.findAllById(ids);
    }

    @Override
    public void delete(OrganizationUnitDB organizationUnit) {
        organizationUnitJpaRepository.delete(organizationUnit);
    }
}
