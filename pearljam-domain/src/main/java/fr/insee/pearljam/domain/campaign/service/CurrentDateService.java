package fr.insee.pearljam.domain.campaign.service;


import fr.insee.pearljam.domain.campaign.port.in.DateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CurrentDateService implements DateService {
    private final Clock clock;
    
    @Override
    public long getCurrentTimestamp() {
        return clock.instant().toEpochMilli();
    }

    @Override
    public Instant now() {
        return clock.instant();
    }
}

