package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.api.service.UtilsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class UtilsFakeService implements UtilsService {

    @Override
    public List<String> getRelatedOrganizationUnits(String userId) {
        return List.of();
    }

    @Override
    public ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, List<String> id) {
        return null;
    }
}
