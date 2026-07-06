package fr.insee.pearljam.api.reporting.export.campaignorganization;

import fr.insee.pearljam.api.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CampaignOrganizationCsvExporter extends AbstractCsvExporter {

    private final CampaignOrganizationPort campaignOrganizationPort;
    private final CampaignOrganizationCsvPresenter presenter;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date) {
        CampaignOrganizationCsv csv = campaignOrganizationPort.getCampaignOrganization(
                userId, campaignId, presenter);
        String label = csv.campaignId() + "_Repartition_enqueteurs";
        return buildResponse(csv, label, date);
    }
}
