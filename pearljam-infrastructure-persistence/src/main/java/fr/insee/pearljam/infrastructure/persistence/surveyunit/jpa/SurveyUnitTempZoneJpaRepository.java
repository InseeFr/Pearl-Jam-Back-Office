package fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitTempZoneDB;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SurveyUnitTempZoneRepository is the repository using to access to
 * SurveyUnitTempZone table in DB
 * 
 * @author Laurent Caouissin
 * 
 */
public interface SurveyUnitTempZoneJpaRepository extends JpaRepository<SurveyUnitTempZoneDB, UUID> {

    void deleteBySurveyUnitId(String id);
}
