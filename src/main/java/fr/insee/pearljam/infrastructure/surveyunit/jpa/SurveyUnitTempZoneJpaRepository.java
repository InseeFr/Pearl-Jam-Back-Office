package fr.insee.pearljam.infrastructure.surveyunit.jpa;

import fr.insee.pearljam.domain.surveyunit.model.SurveyUnitTempZone;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SurveyUnitTempZoneRepository is the repository using to access to
 * SurveyUnitTempZone table in DB
 * 
 * @author Laurent Caouissin
 * 
 */
public interface SurveyUnitTempZoneJpaRepository extends JpaRepository<SurveyUnitTempZone, UUID> {

    void deleteBySurveyUnitId(String id);
}
