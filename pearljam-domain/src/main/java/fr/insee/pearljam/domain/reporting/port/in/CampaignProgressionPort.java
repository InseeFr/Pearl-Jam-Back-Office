package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.model.CampaignProgression;

import java.time.Instant;
import java.util.List;


public interface CampaignProgressionPort {

    List<CampaignProgression> getCampaignsProgression(String userId, Instant date);
}
