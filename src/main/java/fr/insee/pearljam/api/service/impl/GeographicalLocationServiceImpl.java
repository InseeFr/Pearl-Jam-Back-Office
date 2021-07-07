package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.GeographicalLocation;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
import fr.insee.pearljam.api.repository.GeographicalLocationRepository;
import fr.insee.pearljam.api.service.GeographicalLocationService;

/**
 * Implementation of the Service for the Interviewer entity
 * 
 * @author scorcaud
 *
 */
@Service
public class GeographicalLocationServiceImpl implements GeographicalLocationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeographicalLocationServiceImpl.class);	

	@Autowired
	GeographicalLocationRepository geographicalLocationRepository;

	@Override
	public Response createGeographicalLocations(List<GeographicalLocationDto> geographicalLocations) {
		List<String> errors = new ArrayList<>();
		List<GeographicalLocation> lstGeographicalLocation = new ArrayList<>();
		// Check duplicate line in geographicalLocations to create
		geographicalLocations.stream().forEach(gl -> {
			if(!gl.isValid()) {
				errors.add(gl.getLabel());
			}
			lstGeographicalLocation.add(new GeographicalLocation(gl.getId(), gl.getLabel()));
		});	
		if(!errors.isEmpty()) {
			LOGGER.info("Invalid value : [{}]", String.join(", ", errors));
			return new Response(String.format("Invalid values : %s", String.join(", ", errors)), HttpStatus.BAD_REQUEST);
		}
		geographicalLocationRepository.saveAll(lstGeographicalLocation);
		LOGGER.info("{} geographical-locations created", geographicalLocations.size());
		return new Response(String.format("%s geographical-locations created", geographicalLocations.size()), HttpStatus.OK);
	}
	
}
