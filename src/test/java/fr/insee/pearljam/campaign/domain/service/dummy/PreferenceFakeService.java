package fr.insee.pearljam.campaign.domain.service.dummy;

import fr.insee.pearljam.organization.domain.port.userside.PreferenceService;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PreferenceFakeService implements PreferenceService {
    @Override
    public HttpStatus setPreferences(List<String> listPreference, String userId) {
        return null;
    }
}
