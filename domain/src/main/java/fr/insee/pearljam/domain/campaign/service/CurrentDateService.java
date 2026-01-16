package fr.insee.pearljam.domain.campaign.service;


import fr.insee.pearljam.domain.campaign.port.userside.DateService;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CurrentDateService implements DateService {
    @Override
    public long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }
}
