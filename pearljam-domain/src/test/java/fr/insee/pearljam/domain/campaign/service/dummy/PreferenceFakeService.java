package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.campaign.port.in.PreferenceService;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PreferenceFakeService implements PreferenceService {
    @Override
    public HttpStatus setPreferences(List<String> listPreference, String userId) {
        return null;
    }

    @Override
    public void deletePreferences(String userId) throws EntityNotFoundException {
        // no-impl
    }
}
