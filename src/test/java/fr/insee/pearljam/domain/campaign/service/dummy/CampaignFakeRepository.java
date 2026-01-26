package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
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

public class CampaignFakeRepository implements CampaignRepository {

    private final List<Campaign> campaigns = new ArrayList<>();

    @Getter
    private Campaign savedCampaign;

    @Override
    public Optional<Campaign> findByIdIgnoreCase(String id) {
        return campaigns.stream()
                .filter(campaign -> campaign.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<String> findAllCampaignIdsByOuIds(List<String> ouIds) {
        return List.of();
    }

    @Override
    public List<String> findIdsByOuId(String ouId) {
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
    public List<CampaignDto> findAllDtoByOuIds(List<String> ouIds) {
        return List.of();
    }

    @Override
    public List<Integer> checkCampaignPreferences(String userId, String campaignId) {
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

    }

    @Override
    public <S extends Campaign> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Campaign> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Campaign> entities) {
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Campaign getOne(String s) {
        return null;
    }

    @Override
    public Campaign getById(String s) {
        return null;
    }

    @Override
    public Campaign getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Campaign> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Campaign> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Campaign> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Campaign> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Campaign> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Campaign> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Campaign, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Campaign> S save(S campaign) {
        savedCampaign = campaign;
        Optional<Campaign> campaignToUpdate = findById(campaign.getId());
        if(campaignToUpdate.isPresent()) {
            campaigns.remove(campaignToUpdate);
        }
        campaigns.add(campaign);
        return campaign;
    }

    @Override
    public <S extends Campaign> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Campaign> findById(String id) {
        return findByIdIgnoreCase(id);
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<Campaign> findAll() {
        return List.of();
    }

    @Override
    public List<Campaign> findAllById(Iterable<String> strings) {
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
    public void delete(Campaign entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Campaign> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Campaign> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return null;
    }

    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
    }
}
