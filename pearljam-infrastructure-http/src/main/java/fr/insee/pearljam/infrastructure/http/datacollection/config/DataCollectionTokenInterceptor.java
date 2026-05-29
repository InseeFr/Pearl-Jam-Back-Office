package fr.insee.pearljam.infrastructure.http.datacollection.config;

import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Add interceptor to restclient to inject tokens when oidc is enabled
 */
@RequiredArgsConstructor
public class DataCollectionTokenInterceptor implements ClientHttpRequestInterceptor {

    private final AuthenticatedUserService authenticationHelper;

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte @NonNull [] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        String jwt = authenticationHelper.getToken();
        headers.setBearerAuth(jwt);
        return execution.execute(request, body);
    }
}
