package fr.insee.pearljam.jms.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "feature.multimode")
public class MultimodeProperties {

    private Subscriber subscriber = new Subscriber();

    private String topic = "multimode_events";

    @Getter
    @Setter
    public static class Subscriber {
        private boolean enabled = false;
    }

}