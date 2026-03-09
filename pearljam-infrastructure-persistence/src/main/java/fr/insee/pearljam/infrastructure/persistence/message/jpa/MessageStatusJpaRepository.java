package fr.insee.pearljam.infrastructure.persistence.message.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageStatusDB;
import fr.insee.pearljam.infrastructure.persistence.message.entity.MessageStatusDBId;

/**
* MessageRepository is the repository using to access to Message table in DB
* 
* @author Guillemet Paul
* 
*/
public interface MessageStatusJpaRepository extends JpaRepository<MessageStatusDB, MessageStatusDBId> {


}
