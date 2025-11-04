package fr.insee.pearljam.message.infrastructure.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.message.infrastructure.persistence.jpa.entity.MessageStatus;
import fr.insee.pearljam.message.infrastructure.persistence.jpa.entity.MessageStatusId;

/**
* MessageRepository is the repository using to access to Message table in DB
* 
* @author Guillemet Paul
* 
*/
public interface MessageStatusRepository extends JpaRepository<MessageStatus, MessageStatusId> {


}
