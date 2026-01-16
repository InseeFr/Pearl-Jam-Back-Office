package fr.insee.pearljam.jms.configuration;

import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableJms
@EnableScheduling
@Configuration(enforceUniqueMethods = false)
public class JMSConfiguration {

    @Bean("topicJmsListenerContainerFactory")
    @ConditionalOnProperty(
            prefix = "broker",
            name = "name",
            havingValue = "artemis",
            matchIfMissing = false
    )
    public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true); // Enable topic mode for listeners
        log.info("JmsListenerContainerFactory configured for topics (pub/sub mode)");
        return factory;
    }

}
