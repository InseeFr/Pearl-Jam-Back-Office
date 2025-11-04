package fr.insee.pearljam.configuration.web.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackAvoidLogForgeryContentEncoder extends MessageConverter {
    @Override
    public String convert(ILoggingEvent event) {
        String content = super.convert(event);
        return content
                .replace('\n', '_')
                .replace('\r', '_');
    }
}
