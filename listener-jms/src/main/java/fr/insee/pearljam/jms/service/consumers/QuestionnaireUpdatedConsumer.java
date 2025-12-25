package fr.insee.pearljam.jms.service.consumers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.pearljam.domain.surveyunit.port.userside.StatusService;
import fr.insee.pearljam.jms.service.EventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireUpdatedConsumer implements EventConsumer {
    private final StatusService statusService;

    @Override
    public void consume(EventDto eventDto) {
        if(EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED != eventDto.getEventType()){
            return;
        }
        this.statusService.updateStatus(eventDto.getPayload().getInterrogationId(), eventDto.getEventType().toString());
    }
}
