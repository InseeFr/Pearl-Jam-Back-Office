package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.GeographicalLocation;

/**
* GeographicalLocationRepository is the repository using to access to GeographicalLocation table in DB
* 
* @author scorcaud
* 
*/
public interface GeographicalLocationRepository extends JpaRepository<GeographicalLocation, String> {

}
