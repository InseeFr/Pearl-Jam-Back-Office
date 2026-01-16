package fr.insee.pearljam.jms.service.consumers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.pearljam.domain.surveyunit.port.userside.MovedService;
import fr.insee.pearljam.domain.surveyunit.port.userside.StatusService;
import fr.insee.pearljam.jms.service.EventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MultiModeMovedConsumer implements EventConsumer {
    private final MovedService movedService;
    private final StatusService statusService;
    @Override
    public void consume(EventDto eventDto) {
        if(EventDto.EventTypeEnum.MULTIMODE_MOVED != eventDto.getEventType()){
            return;
        }

        var interrogationId = eventDto.getPayload().getInterrogationId();
        this.statusService.updateStatus(interrogationId, eventDto.getEventType().toString());
        this.movedService.updateMovedSurveyUnit(interrogationId);
    }
}
