package fr.insee.pearljam.campaign.domain.service.dummy;

import fr.insee.pearljam.campaign.domain.port.userside.DateService;

public class FixedDateService implements DateService {
    public static final long FIXED_TIMESTAMP = 1719324512000L;
    @Override
    public long getCurrentTimestamp() {
        return FIXED_TIMESTAMP;
    }
}
