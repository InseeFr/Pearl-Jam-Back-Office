package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.campaign.port.in.DateService;

import java.time.Instant;

public class DateServiceStub implements DateService {

    @Override
    public long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }
}
