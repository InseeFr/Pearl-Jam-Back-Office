package fr.insee.pearljam.infrastructure.persistence.campaign.adapter;

import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CampaignDaoAdapter implements CampaignRepository {

    private final CampaignJpaRepository campaignJpaRepository;
    private final EntityManager em;

    private static final String JPQL_CAMPAIGN_WITH_VISIBILITY = """
            SELECT new fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility(
                camp.id,
                camp.label,
                MIN(vi.managementStartDate),
                MIN(vi.interviewerStartDate),
                MIN(vi.identificationPhaseStartDate),
                MIN(vi.collectionStartDate),
                MAX(vi.collectionEndDate),
                MAX(vi.endDate)
            )
            FROM CampaignDB camp
            JOIN camp.visibilities vi
            JOIN vi.organizationUnit ou
            WHERE vi.managementStartDate <= :date
            AND vi.endDate > :date
            AND NOT EXISTS (
                SELECT 1
                FROM UserDB u
                JOIN u.campaigns c2
                WHERE LOWER(u.id) = LOWER(:userId)
                AND c2 = camp
            )
            AND ou.id in (:ouIds)
            GROUP BY camp.id, camp.label
            """;

    @Override
    public Optional<CampaignDB> findById(String id) {
        return campaignJpaRepository.findById(id);
    }

    @Override
    public Optional<CampaignDB> findByIdIgnoreCase(String id) {
        return campaignJpaRepository.findByIdIgnoreCase(id);
    }

    @Override
    public boolean existsById(String id) {
        return campaignJpaRepository.existsById(id);
    }

    @Override
    public List<String> findAllCampaignIdsByOuIds(List<String> ouIds) {
        return campaignJpaRepository.findAllCampaignIdsByOuIds(ouIds);
    }

    @Override
    public List<CampaignSummary> findAllManagedAndNotClosedCampaignsByOuIds(List<String> ouIds, Instant date) {
        return campaignJpaRepository.findAllManagedAndNotClosedCampaignByOuIds(ouIds, date.toEpochMilli());
    }

    @Override
    public List<String> findAllManagedAndNotClosedCampaignIdsByOuIds(List<String> ouIds, Long date) {
        return campaignJpaRepository.findAllManagedAndNotClosedCampaignIdsByOuIds(ouIds, date);
    }

    @Override
    public List<CampaignDto> findByUserAndManagementVisibility(List<String> ouIds, String userId, Long date) {
        return campaignJpaRepository.findByUserAndManagementVisibility(ouIds, userId, date);
    }

    @Override
    public List<CampaignWithVisibility> findCampaignWithVisibilityByUserAndManagementVisibility(
            List<String> ouIds, String userId, Long date) {
        return em.createQuery(JPQL_CAMPAIGN_WITH_VISIBILITY, CampaignWithVisibility.class)
                .setParameter("ouIds", ouIds)
                .setParameter("userId", userId)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    public List<CampaignPreferenceDto> findByOuIdWithPreference(List<String> ouIds, String userId, Long date) {
        return campaignJpaRepository.findByOuIdWithPreference(ouIds, userId, date);
    }

    @Override
    public CampaignDto findDtoById(String id) {
        return campaignJpaRepository.findDtoById(id);
    }

    @Override
    public CampaignDto findDtoBySurveyUnitId(String id) {
        return campaignJpaRepository.findDtoBySurveyUnitId(id);
    }

    @Override
    public List<CampaignDto> findAllDto() {
        return campaignJpaRepository.findAllDto();
    }

    @Override
    public List<CampaignDto> findAllDtoByOuIds(List<String> ouIds) {
        return campaignJpaRepository.findAllDtoByOuIds(ouIds);
    }

    @Override
    public List<CampaignDB> findAll() {
        return campaignJpaRepository.findAll();
    }

    @Override
    public List<CampaignDB> findAllById(Iterable<String> ids) {
        return campaignJpaRepository.findAllById(ids);
    }

    @Override
    public CampaignDB save(CampaignDB campaign) {
        return campaignJpaRepository.save(campaign);
    }

    @Override
    public void delete(CampaignDB campaign) {
        campaignJpaRepository.delete(campaign);
    }

    @Override
    public List<String> findAllOrganistionUnitIdByCampaignId(String campaignId) {
        return campaignJpaRepository.findAllOrganistionUnitIdByCampaignId(campaignId);
    }

    @Override
    public List<VerifyNameResponseDto> findMatchingCampaigns(String text, List<String> ouIds, Long date, Pageable pageable) {
        return campaignJpaRepository.findMatchingCampaigns(text, ouIds, date, pageable);
    }
}
