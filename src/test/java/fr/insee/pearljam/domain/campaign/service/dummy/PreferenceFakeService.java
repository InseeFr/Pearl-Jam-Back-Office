package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.service.PreferenceService;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PreferenceFakeService implements PreferenceService {
    @Override
    public HttpStatus setPreferences(List<String> listPreference, String userId) {
        return null;
    }
}
