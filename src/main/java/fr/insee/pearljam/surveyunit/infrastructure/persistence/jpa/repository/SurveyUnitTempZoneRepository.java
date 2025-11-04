package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnitTempZone;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SurveyUnitTempZoneRepository is the repository using to access to
 * SurveyUnitTempZone table in DB
 * 
 * @author Laurent Caouissin
 * 
 */
public interface SurveyUnitTempZoneRepository extends JpaRepository<SurveyUnitTempZone, UUID> {

    public void deleteBySurveyUnitId(String id);
}
