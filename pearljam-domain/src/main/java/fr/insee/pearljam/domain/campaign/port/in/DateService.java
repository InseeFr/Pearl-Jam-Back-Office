package fr.insee.pearljam.domain.campaign.port.in;

import java.time.Instant;

public interface DateService {
    /**
     * @return current timestamp
     */
    long getCurrentTimestamp();

    Instant now();
}
