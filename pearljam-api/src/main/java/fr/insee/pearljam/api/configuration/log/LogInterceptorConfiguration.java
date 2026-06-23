package fr.insee.pearljam.api.configuration.log;

import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Add the log interceptor to the app
 */
@Configuration
@RequiredArgsConstructor
public class LogInterceptorConfiguration implements WebMvcConfigurer {
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.myLogInterceptor(authenticatedUserService));
    }

    @Bean
    public LogInterceptor myLogInterceptor(AuthenticatedUserService authenticationUserService) {
        return new LogInterceptor(authenticationUserService);
    }
}
