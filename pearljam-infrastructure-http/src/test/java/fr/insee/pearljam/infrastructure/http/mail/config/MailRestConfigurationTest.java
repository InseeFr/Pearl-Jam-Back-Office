package fr.insee.pearljam.infrastructure.http.mail.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class MailRestConfigurationTest {
    private RestClient.Builder restClientBuilder;
    private MailProperties mailProperties;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        mailProperties = new MailProperties("url", "login", "password", "recipient1,recipient2", "sender");
        MailRestConfiguration conf = new MailRestConfiguration(mailProperties);
        restClientBuilder = conf.mailRestClient();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
    }

    @Test
    @DisplayName("when using mail restClient, assure basic authentication is integrated in requests")
    void testAuthorizationIsIntegratedInHttpRequest() throws URISyntaxException {

        String base64Credentials = Base64.getEncoder()
                .encodeToString((mailProperties.login() + ":" + mailProperties.password()).getBytes());

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(mailProperties.url())))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Basic " + base64Credentials))
                .andRespond(withStatus(HttpStatus.OK));

        RestClient restClient = restClientBuilder.build();

        restClient.get()
                .retrieve()
                .toBodilessEntity();

        mockServer.verify();
    }
}
