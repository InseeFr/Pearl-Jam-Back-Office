package fr.insee.pearljam.domain.campaign.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.Referent;

import java.util.List;

public interface CampaignReferentRepository {
    List<Referent> getReferents(String campaignId);
}
