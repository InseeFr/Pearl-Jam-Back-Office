package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.SurveyUnitTempZone;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
* SurveyUnitTempZoneRepository is the repository using to access to SurveyUnitTempZone table in DB
* 
* @author Laurent Caouissin
* 
*/
public interface SurveyUnitTempZoneRepository extends JpaRepository<SurveyUnitTempZone, String> {
}
