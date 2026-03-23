package fr.insee.pearljam.infrastructure.persistence.reporting.jpa;

import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignProgressionJpaRepository  extends JpaRepository<UserDB, String> {
}
