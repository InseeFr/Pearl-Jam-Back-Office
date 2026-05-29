package fr.insee.pearljam.infrastructure.http.mail.config;

import fr.insee.pearljam.infrastructure.http.mail.sender.RestMailSender;
import fr.insee.pearljam.domain.message.service.exception.SendMailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

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
        mailProperties = new MailProperties("http://dummy-url/send-mail", "login", "password", "recipients", "mailSender");
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl(mailProperties.url());
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        mailSender = new RestMailSender(mailProperties, restClientBuilder);
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
