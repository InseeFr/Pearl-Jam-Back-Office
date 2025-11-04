package fr.insee.pearljam.campaign.domain.service.dummy;

import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.organization.domain.port.userside.UtilsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UtilsFakeService implements UtilsService {
    @Override
    public boolean checkUserCampaignOUConstraints(String userId, String campaignId) {
        return false;
    }

    @Override
    public List<String> getRelatedOrganizationUnits(String userId) {
        return List.of();
    }

    @Override
    public ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, List<String> id) {
        return null;
    }
}
