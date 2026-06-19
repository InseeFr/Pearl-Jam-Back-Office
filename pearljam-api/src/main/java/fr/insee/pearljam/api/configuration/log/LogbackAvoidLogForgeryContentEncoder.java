package fr.insee.pearljam.api.configuration.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackAvoidLogForgeryContentEncoder extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        String content = super.convert(event);
        if (content == null) {
            return "";
        }
        return content
                .replace('\n', '_')
                .replace('\r', '_');
    }
}
