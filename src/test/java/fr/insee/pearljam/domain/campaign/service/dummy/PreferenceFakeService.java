package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.domain.preference.port.userside.PreferenceService;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PreferenceFakeService implements PreferenceService {
    @Override
    public HttpStatus setPreferences(List<String> listPreference, String userId) {
        return null;
    }

    @Override
    public void deletePreferences(String userId) throws NotFoundException {
        // no-impl
    }
}
