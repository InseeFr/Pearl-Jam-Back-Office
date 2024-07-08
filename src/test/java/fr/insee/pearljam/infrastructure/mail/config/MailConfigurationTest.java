package fr.insee.pearljam.infrastructure.mail.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class MailConfigurationTest {
    private RestTemplate restTemplate;
    private MailProperties mailProperties;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void init() {
        mailProperties = new MailProperties("url", "login", "password", "recipient1,recipient2", "sender");
        MailConfiguration conf = new MailConfiguration(mailProperties);
        restTemplate = conf.mailRestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("when using mail restTemplate, assure basic authentication is integrated in requests")
    void testAuthorizationIsIntegratedInHttpRequest() throws URISyntaxException {
        String base64Credentials = Base64.getEncoder().encodeToString((mailProperties.login() + ":" + mailProperties.password()).getBytes());
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("/")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Basic " + base64Credentials))
                .andRespond(withStatus(HttpStatus.OK));

        restTemplate.getForObject("/", Object.class);
        mockServer.verify();
    }
}
