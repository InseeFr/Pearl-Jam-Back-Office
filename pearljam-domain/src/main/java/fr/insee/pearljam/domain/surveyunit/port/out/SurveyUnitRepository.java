package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.SurveyUnitCampaignView;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SurveyUnitRepository {
    List<String> findIdsByInterviewerIdWithinVisibilityScope(String interviewerId, Long now, List<String> visibleTypes);

    Integer findCountUeINATBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit);

    Optional<SurveyUnitDB> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId);

    List<String> findAllIds();

    List<ClosableSurveyUnitCandidateView> findClosableCandidates(long date, List<String> lstOuIds);

    Set<SurveyUnitCampaignView> findByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId);

    Set<SurveyUnitCampaignView> findByCampaignIdAndStateAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId, String state);

    Set<SurveyUnitCampaignView> findFinalizedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId);

    Set<SurveyUnitCampaignView> findClosedByCampaignIdAndOrganizationUnitIdIn(String campaignId, List<String> lstOuId);

    List<SurveyUnitDB> findByInterviewerIdIgnoreCase(String id);

    List<Object[]> getCampaignStats(String campaignId, List<String> organizationalUnitIds);

    List<SurveyUnitDB> findByOrganizationUnitIdIn(List<String> lstOuId);

    List<SurveyUnitDB> findByIdInOrganizationalUnit(String id, List<String> organizationalUnitIds);

    Collection<SurveyUnitDB> findByCampaignId(String id);

    List<String> findAllIdsByCampaignId(String campaignId);

    List<String> findAllIdsByInterviewerId(String interviewerId);

    void setInterviewer(List<String> surveyUnitIds, String interviewerId);

    List<ClosableSurveyUnitView> findClosableSurveyUnits(Set<String> ids);

    Optional<SurveyUnitDB> findById(String surveyUnitId);

    SurveyUnitDB save(SurveyUnitDB surveyUnit);

    List<SurveyUnitDB> saveAll(List<SurveyUnitDB> surveyUnits);

    List<SurveyUnitDB> findAllById(Iterable<String> ids);

    void deleteById(String surveyUnitId);

    List<String> findExistingIds(List<String> surveyUnitIds);

    /**
     * Récupère les IDs de toutes les survey units éligibles à la fermeture
     * (après application des règles de visibilité et de campagne).
     * Utilisé pour la pagination.
     *
     * @param date timestamp actuel
     * @param lstOuIds liste des Organization Unit IDs de l'utilisateur
     * @return liste des IDs des survey units éligibles
     */
    List<String> findEligibleSurveyUnitIds(long date, List<String> lstOuIds);

    /**
     * Compte le nombre total de survey units éligibles à la fermeture.
     * Utilisé pour la pagination.
     *
     * @param date timestamp actuel
     * @param lstOuIds liste des Organization Unit IDs de l'utilisateur
     * @return nombre total d'éléments éligibles
     */
    long countEligibleSurveyUnits(long date, List<String> lstOuIds);

    /**
     * Récupère les ClosableSurveyUnitCandidateView pour une liste spécifique d'IDs.
     * Utilisé pour la pagination.
     *
     * @param ids liste des IDs des survey units
     * @param date timestamp actuel
     * @return liste des candidats correspondants
     */
    List<ClosableSurveyUnitCandidateView> findClosableCandidatesByIds(List<String> ids, long date);
}
