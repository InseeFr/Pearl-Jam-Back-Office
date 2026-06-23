package fr.insee.pearljam.infrastructure.http.mail.config;

import fr.insee.pearljam.infrastructure.http.mail.sender.NoMailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NoMailSenderTest {
    private NoMailSender mailSender;

    @BeforeEach
    void setup() {
        mailSender = new NoMailSender();
    }

    @Test
    @DisplayName("Should do nothing")
    void testSendMail01() {
        assertDoesNotThrow(() -> mailSender.sendMail("subject", "content"));
    }
}
