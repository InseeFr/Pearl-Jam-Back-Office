package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class SurveyUnitFakeRepository implements SurveyUnitRepository {
    @Override
    public List<String> findIdsByInterviewerIdWithinVisibilityScope(String idInterviewer, Long anyTime, List<String> anyStateTypes) {
        return List.of();
    }

    @Override
    public Integer findCountUeINATBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit) {
        return 0;
    }

    @Override
    public Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId) {
        return Optional.empty();
    }

    @Override
    public List<String> findAllIds() {
        return List.of();
    }

    @Override
    public List<SurveyUnit> findSurveyUnitsOfOrganizationUnitsInProcessingPhase(Long date, List<String> lstOuId) {
        return List.of();
    }

    @Override
    public Set<SurveyUnit> findByCampaignIdAndOrganizationUnitIdIn(String id, List<String> lstOuId) {
        return Set.of();
    }

    @Override
    public List<SurveyUnit> findByInterviewerIdIgnoreCase(String id) {
        return List.of();
    }

    @Override
    public List<Object[]> getCampaignStats(String campaignId, List<String> organizationalUnitIds) {
        return List.of();
    }

    @Override
    public List<SurveyUnit> findByOrganizationUnitIdIn(List<String> lstOuId) {
        return List.of();
    }


    @Override
    public List<SurveyUnit> findByIdInOrganizationalUnit(String id, List<String> organizationalUnitIds) {
        return List.of();
    }

    @Override
    public Collection<SurveyUnit> findByCampaignId(String id) {
        return List.of();
    }

    @Override
    public List<String> findAllIdsByCampaignId(String campaignId) {
        return List.of();
    }

    @Override
    public List<String> findAllIdsByInterviewerId(String interviewerId) {
        return List.of();
    }

    @Override
    public void setInterviewer(List<String> surveyUnitIds, String interviewerId) {
        //not used yet
    }

  @Override
    public void flush() {
        //not used yet
    }

    @Override
    public <S extends SurveyUnit> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends SurveyUnit> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<SurveyUnit> entities) {
        //not used yet
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
        //not used yet
    }

    @Override
    public void deleteAllInBatch() {
        //not used yet
    }

    @Override
    public SurveyUnit getOne(String s) {
        return null;
    }

    @Override
    public SurveyUnit getById(String s) {
        return null;
    }

    @Override
    public SurveyUnit getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends SurveyUnit> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends SurveyUnit> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends SurveyUnit> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends SurveyUnit> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends SurveyUnit> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends SurveyUnit> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends SurveyUnit, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends SurveyUnit> S save(S entity) {
        return null;
    }

    @Override
    public <S extends SurveyUnit> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<SurveyUnit> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<SurveyUnit> findAll() {
        return List.of();
    }

    @Override
    public List<SurveyUnit> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {
        //not used yet
    }

    @Override
    public void delete(SurveyUnit entity) {
        //not used yet
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        //not used yet
    }

    @Override
    public void deleteAll(Iterable<? extends SurveyUnit> entities) {
        //not used yet
    }

    @Override
    public void deleteAll() {
        //not used yet
    }

    @Override
    public List<SurveyUnit> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<SurveyUnit> findAll(Pageable pageable) {
        return null;
    }
}
