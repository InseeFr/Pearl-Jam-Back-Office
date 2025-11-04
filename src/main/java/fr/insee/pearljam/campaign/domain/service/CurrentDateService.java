package fr.insee.pearljam.campaign.domain.service;


import fr.insee.pearljam.campaign.domain.port.userside.DateService;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CurrentDateService implements DateService {
    @Override
    public long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }
}
