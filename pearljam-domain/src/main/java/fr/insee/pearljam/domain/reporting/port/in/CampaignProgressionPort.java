package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignProgression;

import java.time.LocalDate;
import java.util.List;


public interface CampaignProgressionPort {

    List<CampaignProgression> getCampaignsProgression(String userId, LocalDate day);
}
