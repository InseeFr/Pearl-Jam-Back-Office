package fr.insee.pearljam.api.configuration;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import fr.insee.pearljam.api.service.DataSetInjectorService;

@ConditionalOnProperty(name = "feature.enableDataset", havingValue = "true")
@Configuration
@RequiredArgsConstructor
public class DataInjectorOnStartup {

    private final DataSetInjectorService injector;

    @EventListener(ApplicationReadyEvent.class)
    public void createDataSetOnStartup() {
        injector.createDataSet();
    }
}
