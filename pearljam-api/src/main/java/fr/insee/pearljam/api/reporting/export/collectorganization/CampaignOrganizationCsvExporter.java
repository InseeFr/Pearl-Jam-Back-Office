package fr.insee.pearljam.api.reporting.export.collectorganization;

import fr.insee.pearljam.api.campaign.presenter.CampaignOrganizationPresenter;
import fr.insee.pearljam.api.campaign.response.CampaignOrganizationResponse;
import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CampaignOrganizationCsvExporter extends AbstractCsvExporter {

    private final CampaignOrganizationPort campaignOrganizationPort;
    private final CampaignOrganizationPresenter presenter;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date) {
        CampaignOrganizationResponse response = campaignOrganizationPort.getCampaignOrganization(
                userId, campaignId, presenter);
        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(response);
        String label = response.campaignLabel() + "_Repartition_enqueteurs";
        return buildResponse(csv, label, date);
    }
}
