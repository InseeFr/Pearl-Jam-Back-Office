package fr.insee.pearljam.api.message.controller;

import fr.insee.pearljam.api.controller.MessageController;
import fr.insee.pearljam.api.message.controller.dummy.MailFakeSender;
import fr.insee.pearljam.api.message.controller.dummy.MessageFakeService;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageControllerTest {

    private MailFakeSender mailSender;
    private MockMvc mockMvc;
    private final String mailPath = "/api/mail";

    @BeforeEach
    void setup() {
        SimpMessagingTemplate simpMessagingTemplate = null;
        mailSender = new MailFakeSender();
        MessageFakeService messageService = new MessageFakeService();
        AuthenticationUserFakeService authenticatedUserService = new AuthenticationUserFakeService(AuthenticatedUserTestHelper.AUTH_ADMIN);
        MessageController messageController = new MessageController(messageService, simpMessagingTemplate, authenticatedUserService, mailSender);

        mockMvc = MockMvcBuilders
                .standaloneSetup(messageController)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should call send mail service")
    void testMail01() throws Exception {
        String mail = """
                {
                    "content": "content",
                    "subject": "subject"
                }
                """;

        mockMvc.perform(post(mailPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mail))
                .andExpect(status().isOk());

        assertThat(mailSender.isMailSent()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when problem with mail sending")
    void testMail02() throws Exception {
        mailSender.setThrowSendMailException(true);
        String mail = """
                {
                    "content": "content",
                    "subject": "subject"
                }
                """;

        mockMvc.perform(post(mailPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mail))
                .andExpectAll(status().isInternalServerError(),
                        MockMvcTestUtils
                                .apiErrorMatches(HttpStatus.INTERNAL_SERVER_ERROR, mailPath, ExceptionControllerAdvice.ERROR_OCCURRED_LABEL)
                );

        assertThat(mailSender.isMailSent()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"subject\": \"subject\"}","{\"content\": \"content\"}"})
    @DisplayName("Should throw exception when attributes are invalid")
    void testMail03(String invalidMail) throws Exception {
        mockMvc.perform(post(mailPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidMail))
                .andExpect(MockMvcTestUtils
                                .apiErrorMatches(HttpStatus.BAD_REQUEST, mailPath, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE)
                );

        assertThat(mailSender.isMailSent()).isFalse();
    }
}
