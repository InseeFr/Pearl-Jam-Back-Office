package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.projection.CampaignProgressionProjection;

import java.time.Instant;
import java.util.List;


public interface CampaignProgression {

    public List<CampaignProgressionProjection> getCampaignsProgression(String userId, Instant date);
}
