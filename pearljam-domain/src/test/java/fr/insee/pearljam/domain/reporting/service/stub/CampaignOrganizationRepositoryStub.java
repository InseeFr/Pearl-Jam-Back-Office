package fr.insee.pearljam.domain.reporting.service.stub;

import fr.insee.pearljam.domain.campaign.port.out.CampaignOrganizationRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;

import java.util.List;
import java.util.Objects;

public class CampaignOrganizationRepositoryStub implements CampaignOrganizationRepository {
        private final List<CampaignWithVisibility> campaigns;
        private final List<Referent> referents;

        public CampaignOrganizationRepositoryStub(List<CampaignWithVisibility> campaign, List<Referent> referents) {
            this.campaigns = campaign;
            this.referents = referents;
        }

        public CampaignWithVisibility findCampaignVisibility(String campaignId, List<String> ouIds, String userId) {
            return campaigns.stream().filter(c -> Objects.equals(c.id(), campaignId)).findFirst().orElse(null);
        }

        public List<Referent> getReferents(String campaignId) {
            return referents;
        }
}
