package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.campaign.port.in.DateService;

import java.time.Instant;

public class FixedDateService implements DateService {

    public static final long FIXED_TIMESTAMP = 1735689600000L;

    @Override
    public long getCurrentTimestamp() {
        return FIXED_TIMESTAMP;
    }

    @Override
    public Instant now() {
        return Instant.ofEpochMilli(FIXED_TIMESTAMP);
    }
}
