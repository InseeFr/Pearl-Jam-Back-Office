package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.domain.count.model.OrganizationUnitLabel;
import lombok.Setter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class OrganizationUnitFakeRepository implements OrganizationUnitRepository {

    @Setter
    private List<OrganizationUnit> organizationUnits = new ArrayList<>();

    @Override
    public Optional<OrganizationUnit> findByIdIgnoreCase(String ouId) {
        return Optional.empty();
    }

    @Override
    public Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(String ouId) {
        return Optional.empty();
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
        return List.of();
    }

    @Override
    public String findLabel(String orgUnitId) {
        return "";
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
    public void flush() {

    }

    @Override
    public <S extends OrganizationUnit> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends OrganizationUnit> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<OrganizationUnit> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public OrganizationUnit getOne(String s) {
        return null;
    }

    @Override
    public OrganizationUnit getById(String s) {
        return null;
    }

    @Override
    public OrganizationUnit getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends OrganizationUnit> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends OrganizationUnit> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends OrganizationUnit> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends OrganizationUnit> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends OrganizationUnit> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends OrganizationUnit> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends OrganizationUnit, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends OrganizationUnit> S save(S entity) {
        return null;
    }

    @Override
    public <S extends OrganizationUnit> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<OrganizationUnit> findById(String ouId) {
        return organizationUnits.stream()
                .filter(organizationUnit -> organizationUnit.getId().equals(ouId))
                .findFirst();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<OrganizationUnit> findAll() {
        return List.of();
    }

    @Override
    public List<OrganizationUnit> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(OrganizationUnit entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends OrganizationUnit> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<OrganizationUnit> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<OrganizationUnit> findAll(Pageable pageable) {
        return null;
    }
}
