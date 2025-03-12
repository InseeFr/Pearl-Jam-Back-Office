package fr.insee.pearljam.infrastructure.campaign.entity;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationTemplateDBTest {

    @Test
    @DisplayName("Should create model objects")
    void testToModel() {
        // Given
        Campaign campaign = new Campaign("id", "label", IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F,
                "email@plop.com");
        CommunicationTemplateDB templateDB1 = new CommunicationTemplateDB(new CommunicationTemplateDBId("mesh1", "SIMPSONS2020X00"), CommunicationMedium.EMAIL, CommunicationType.NOTICE, campaign);
        CommunicationTemplateDB templateDB2 = new CommunicationTemplateDB(new CommunicationTemplateDBId("mesh2", "SIMPSONS2020X00"), CommunicationMedium.LETTER, CommunicationType.REMINDER, campaign);
        List<CommunicationTemplateDB> dbList = List.of(templateDB1, templateDB2);

        // When
        List<CommunicationTemplate> modelList = CommunicationTemplateDB.toModel(dbList);

        // Then
        assertThat(modelList)
                .hasSize(2);

        assertThat(modelList.getFirst().meshuggahId()).isEqualTo("mesh1");
        assertThat(modelList.getFirst().medium()).isEqualTo(CommunicationMedium.EMAIL);
        assertThat(modelList.getFirst().type()).isEqualTo(CommunicationType.NOTICE);

        assertThat(modelList.getLast().meshuggahId()).isEqualTo("mesh2");
        assertThat(modelList.getLast().medium()).isEqualTo(CommunicationMedium.LETTER);
        assertThat(modelList.getLast().type()).isEqualTo(CommunicationType.REMINDER);
    }

    @Test
    @DisplayName("Should create entity objects")
    void testFromModel() {
        // Given
        Campaign campaign = new Campaign("id", "label", IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F,
                "email@plop.com");
        CommunicationTemplate template1 = new CommunicationTemplate("msg1", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        CommunicationTemplate template2 = new CommunicationTemplate("msg2", CommunicationMedium.LETTER, CommunicationType.REMINDER);
        List<CommunicationTemplate> modelList = List.of(template1, template2);

        // When
        List<CommunicationTemplateDB> dbList = CommunicationTemplateDB.fromModel(modelList, campaign);

        // Then
        assertThat(dbList)
                .hasSize(2)
                .anySatisfy(templateDB -> {
                    verifyCommunicationTemplateDB(templateDB, "id",
                            "msg1", CommunicationMedium.EMAIL, CommunicationType.NOTICE, campaign);
                })
                .anySatisfy(templateDB -> {
                    verifyCommunicationTemplateDB(templateDB, "id",
                            "msg2", CommunicationMedium.LETTER, CommunicationType.REMINDER, campaign);
                });
    }

    private void verifyCommunicationTemplateDB(
            CommunicationTemplateDB templateDB,
            String campaignId,
            String expectedMesshugahId,
            CommunicationMedium expectedMedium,
            CommunicationType expectedType,
            Campaign expectedCampaign
    ) {
        assertThat(templateDB.getCommunicationTemplateDBId().getCampaignId()).isEqualTo(campaignId);
        assertThat(templateDB.getCommunicationTemplateDBId().getMeshuggahId()).isEqualTo(expectedMesshugahId);
        assertThat(templateDB.getMedium()).isEqualTo(expectedMedium);
        assertThat(templateDB.getType()).isEqualTo(expectedType);
        assertThat(templateDB.getCampaign()).isEqualTo(expectedCampaign);
    }
}
