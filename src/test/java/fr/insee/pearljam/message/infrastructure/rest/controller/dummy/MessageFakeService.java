package fr.insee.pearljam.message.infrastructure.rest.controller.dummy;

import fr.insee.pearljam.message.infrastructure.rest.dto.MessageDto;
import fr.insee.pearljam.message.infrastructure.rest.dto.VerifyNameResponseDto;
import fr.insee.pearljam.message.domain.port.userside.MessageService;
import org.springframework.http.HttpStatus;

import java.util.List;

public class MessageFakeService implements MessageService {
    @Override
    public HttpStatus addMessage(String text, List<String> recipients, String userId) {
        throw new IllegalArgumentException("not implemented");
    }

    @Override
    public HttpStatus markAsRead(Long id, String idep) {
        throw new IllegalArgumentException("not implemented");
    }

    @Override
    public HttpStatus markAsDeleted(Long id, String idep) {
        throw new IllegalArgumentException("not implemented");
    }

    @Override
    public List<MessageDto> getMessages(String interviewerId) {
        throw new IllegalArgumentException("not implemented");
    }

    @Override
    public List<VerifyNameResponseDto> verifyName(String text, String userId) {
        throw new IllegalArgumentException("not implemented");
    }

    @Override
    public List<MessageDto> getMessageHistory(String userId) {
        throw new IllegalArgumentException("not implemented");
    }

    @Override
    public void deleteMessageByUserId(String userId) {
        throw new IllegalArgumentException("not implemented");
    }
}
