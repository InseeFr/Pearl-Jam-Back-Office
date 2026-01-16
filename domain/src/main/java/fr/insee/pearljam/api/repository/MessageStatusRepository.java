package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.MessageStatus;
import fr.insee.pearljam.api.domain.MessageStatusId;

/**
* MessageRepository is the repository using to access to Message table in DB
* 
* @author Guillemet Paul
* 
*/
public interface MessageStatusRepository extends JpaRepository<MessageStatus, MessageStatusId> {


}
