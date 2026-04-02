package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.collect.CampaignCollection;

import java.time.LocalDate;
import java.util.List;


public interface CampaignCollectionPort {

    List<CampaignCollection> getCampaignsCollection(String userId, LocalDate day);
}
