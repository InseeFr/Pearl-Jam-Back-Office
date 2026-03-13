package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CampaignPreferenceDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;
import fr.insee.pearljam.infrastructure.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.campaign.jpa.CampaignJpaRepository;
import lombok.Getter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CampaignFakeRepository implements CampaignJpaRepository {

    private final List<CampaignDB> campaigns = new ArrayList<>();

    @Getter
    private CampaignDB savedCampaign;

    @Override
    public Optional<CampaignDB> findByIdIgnoreCase(String id) {
        return campaigns.stream()
                .filter(campaign -> campaign.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<String> findAllCampaignIdsByOuIds(List<String> ouIds) {
        return List.of();
    }

    @Override
    public List<CampaignDto> findByUserAndManagementVisibility(List<String> organisationalUnitIds, String userId, Long date) {
        return List.of();
    }

    @Override
    public List<CampaignPreferenceDto> findByOuIdWithPreference(List<String> ouIds, String userId, Long date) {
        return List.of();
    }

    @Override
    public CampaignDto findDtoById(String id) {
        return null;
    }

    @Override
    public CampaignDto findDtoBySurveyUnitId(String id) {
        return null;
    }

    @Override
    public List<CampaignDto> findAllDto() {
        return List.of();
    }

    @Override
    public List<InterviewerDto> findInterviewersDtoByCampaignIdAndOrganisationUnitId(String id, String organizationUnitId) {
        return List.of();
    }

    @Override
    public List<String> findAllOrganistionUnitIdByCampaignId(String campaignId) {
        return List.of();
    }

    @Override
    public List<VerifyNameResponseDto> findMatchingCampaigns(String text, List<String> ouIds, Long date, Pageable pageable) {
        return List.of();
    }

    @Override
    public void flush() {
        // to fill
    }

    @Override
    public <S extends CampaignDB> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends CampaignDB> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<CampaignDB> entities) {
        // to fill
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
        // to fill
    }

    @Override
    public void deleteAllInBatch() {
        // to fill
    }

    @Override
    public CampaignDB getOne(String s) {
        return null;
    }

    @Override
    public CampaignDB getById(String s) {
        return null;
    }

    @Override
    public CampaignDB getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends CampaignDB> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends CampaignDB> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends CampaignDB> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends CampaignDB> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends CampaignDB> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends CampaignDB> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends CampaignDB, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S extends CampaignDB> S save(S campaign) {
        savedCampaign = campaign;
        Optional<CampaignDB> campaignToUpdate = findById(campaign.getId());
        if(campaignToUpdate.isPresent()) {
            campaigns.remove(campaignToUpdate.get());
        }
        campaigns.add(campaign);
        return campaign;
    }

    @Override
    public <S extends CampaignDB> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<CampaignDB> findById(String id) {
        return findByIdIgnoreCase(id);
    }

    @Override
    public boolean existsById(String s) {
        return campaigns.stream().anyMatch(c -> c.getId().equalsIgnoreCase(s));
    }

    @Override
    public List<CampaignDB> findAll() {
        return List.of();
    }

    @Override
    public List<CampaignDB> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {
        // to fill
    }

    @Override
    public void delete(CampaignDB entity) {
        // to fill
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        // to fill
    }

    @Override
    public void deleteAll(Iterable<? extends CampaignDB> entities) {
        // to fill
    }

    @Override
    public void deleteAll() {
        // to fill
    }

    @Override
    public List<CampaignDB> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<CampaignDB> findAll(Pageable pageable) {
        return null;
    }

    public void addCampaign(CampaignDB campaign) {
        campaigns.add(campaign);
    }
}
