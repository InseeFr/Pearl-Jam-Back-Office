package fr.insee.pearljam.infrastructure.mail;

import fr.insee.pearljam.infrastructure.mail.config.MailProperties;
import fr.insee.pearljam.infrastructure.mail.exception.SendMailException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class RestMailSenderTest {
    private RestMailSender mailSender;
    private MailProperties mailProperties;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mailProperties = new MailProperties("http://dummy-url/send-mail", "login", "password", "recipients", "mailSender");
        mailSender = new RestMailSender(mailProperties, restTemplate);
    }

    @Test
    @DisplayName("Should call the send mail service")
    void testSendMail01() throws SendMailException, URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(mailProperties.url())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        mailSender.sendMail("subject", "content");
        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw an exception if an error occurred")
    void testSendMail02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(mailProperties.url())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> mailSender.sendMail("subject", "content"))
                .isInstanceOf(SendMailException.class);
        mockServer.verify();
    }
}
