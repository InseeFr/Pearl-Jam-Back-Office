package fr.insee.pearljam.domain.reporting.service.stub;

import fr.insee.pearljam.domain.campaign.port.out.CampaignReferentRepository;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;

import java.util.List;

public class CampaignReferentRepositoryStub implements CampaignReferentRepository {
        private final List<Referent> referents;

        public CampaignReferentRepositoryStub(List<Referent> referents) {
            this.referents = referents;
        }


        public List<Referent> getReferents(String campaignId) {
            return referents;
        }
}
