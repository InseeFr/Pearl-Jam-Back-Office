package fr.insee.pearljam.infrastructure.contactoutcome.adapter;

import fr.insee.pearljam.domain.contactoutcome.port.serverside.ContactOutcomeRepository;
import fr.insee.pearljam.infrastructure.contactoutcome.jpa.ContactOutcomeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ContactOutcomeDaoAdapter implements ContactOutcomeRepository {
    private final ContactOutcomeJpaRepository contactOutcomeJpaRepository;

    @Override
    public Map<String, Long> findContactOutcomeTypeByInterviewerAndCampaign(String campaignId, String interviewerId, List<String> ouIds, Long date) {
        return contactOutcomeJpaRepository.findContactOutcomeTypeByInterviewerAndCampaign(campaignId, interviewerId, ouIds, date);
    }

    @Override
    public Map<String, Long> findContactOutcomeTypeNotAttributed(String campaignId, List<String> ouIds, Long date) {
        return contactOutcomeJpaRepository.findContactOutcomeTypeNotAttributed(campaignId, ouIds, date);
    }

    @Override
    public Map<String, Long> getContactOutcomeTypeCountByCampaignId(String campaignId, Long date) {
        return contactOutcomeJpaRepository.getContactOutcomeTypeCountByCampaignId(campaignId, date);
    }

    @Override
    public Map<String, Long> getContactOutcomeTypeCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date) {
        return contactOutcomeJpaRepository.getContactOutcomeTypeCountByCampaignAndOU(campaignId, organizationalUnitId, date);
    }
}
