package fr.insee.pearljam.campaign.domain.service.dummy;

import fr.insee.pearljam.message.infrastructure.persistence.jpa.entity.Message;
import fr.insee.pearljam.message.infrastructure.rest.dto.MessageDto;
import fr.insee.pearljam.message.infrastructure.rest.dto.VerifyNameResponseDto;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.repository.MessageRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class MessageFakeRepository implements MessageRepository {
    @Override
    public Optional<Message> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Long> getMessageIdsByInterviewer(String interviewerId) {
        return List.of();
    }

    @Override
    public List<Long> getMessageIdsByOrganizationUnit(List<String> organizationUnitIds) {
        return List.of();
    }

    @Override
    public List<Long> getAllOrganizationMessagesIds(List<String> organizationUnitIds) {
        return List.of();
    }

    @Override
    public List<MessageDto> findMessagesDtoByIds(List<Long> ids) {
        return List.of();
    }

    @Override
    public List<String> getMessageStatus(Long messageId, String interviewerId) {
        return List.of();
    }

    @Override
    public List<VerifyNameResponseDto> getCampaignRecipients(Long messageId) {
        return List.of();
    }

    @Override
    public List<VerifyNameResponseDto> getOuRecipients(Long messageId) {
        return List.of();
    }

    @Override
    public void deleteCampaignMessageRecipientByCampaignId(String campaignId) {

    }

    @Override
    public void deleteCampaignMessageRecipientByMessageId(Long messageId) {

    }

    @Override
    public void deleteOUMessageRecipientByOrganizationUnitId(String organizationUnitId) {

    }

    @Override
    public void deleteOUMessageRecipientByMessageId(Long messageId) {

    }

    @Override
    public List<Message> findAllBySenderId(String userId) {
        return List.of();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Message> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Message> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Message> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Message getOne(Long aLong) {
        return null;
    }

    @Override
    public Message getById(Long aLong) {
        return null;
    }

    @Override
    public Message getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Message> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Message> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Message> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Message> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Message> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Message> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Message, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Message> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Message> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public List<Message> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Message entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Message> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Message> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return null;
    }
}
