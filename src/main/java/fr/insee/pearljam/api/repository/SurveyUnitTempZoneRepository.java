package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.api.domain.SurveyUnitTempZone;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SurveyUnitTempZoneRepository is the repository using to access to
 * SurveyUnitTempZone table in DB
 * 
 * @author Laurent Caouissin
 * 
 */
public interface SurveyUnitTempZoneRepository extends JpaRepository<SurveyUnitTempZone, String> {
}
