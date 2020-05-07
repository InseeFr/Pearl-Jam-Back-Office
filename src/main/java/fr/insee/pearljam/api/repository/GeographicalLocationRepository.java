package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.insee.pearljam.api.domain.GeographicalLocation;

public interface GeographicalLocationRepository extends JpaRepository<GeographicalLocation, String> {

}
