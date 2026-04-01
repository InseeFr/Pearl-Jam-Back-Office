package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummaryProgress;

import java.time.LocalDate;
import java.util.List;


public interface CampaignSummaryProgressPort {

    List<CampaignSummaryProgress> getCampaignSummaryProgression(String userId, LocalDate day);
}
