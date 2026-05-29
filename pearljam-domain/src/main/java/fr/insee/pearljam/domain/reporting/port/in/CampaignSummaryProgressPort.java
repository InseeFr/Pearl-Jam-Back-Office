package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignSummaryProgress;

import java.time.LocalDate;
import java.util.List;


public interface CampaignSummaryProgressPort {

    List<CampaignSummaryProgress> getCampaignSummaryProgress(String userId, LocalDate day);
}
