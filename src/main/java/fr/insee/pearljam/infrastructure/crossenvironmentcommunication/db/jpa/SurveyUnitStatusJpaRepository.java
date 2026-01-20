package fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.jpa;

import fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.entity.SurveyUnitStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyUnitStatusJpaRepository extends JpaRepository<SurveyUnitStatusEntity, String> {
}
