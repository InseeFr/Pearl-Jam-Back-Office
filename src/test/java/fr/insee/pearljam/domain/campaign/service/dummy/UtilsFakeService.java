package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.surveyunit.dto.InterrogationOkNokDto;
import fr.insee.pearljam.domain.common.port.userside.UtilsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public class UtilsFakeService implements UtilsService {

    @Override
    public List<String> getRelatedOrganizationUnits(String userId) {
        return List.of();
    }

    @Override
    public ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, Set<String> id) {
        return null;
    }
}
