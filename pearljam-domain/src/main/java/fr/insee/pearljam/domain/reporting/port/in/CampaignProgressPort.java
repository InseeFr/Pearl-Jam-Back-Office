package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignProgress;

import java.time.LocalDate;
import java.util.List;


public interface CampaignProgressPort {

    List<CampaignProgress> getCampaignsProgress(String userId, LocalDate day);
}
