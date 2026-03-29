package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummaryProgression;

import java.time.LocalDate;
import java.util.List;


public interface CampaignSummaryProgressionPort {

    List<CampaignSummaryProgression> getCampaignSummaryProgression(String userId, LocalDate day);
}
