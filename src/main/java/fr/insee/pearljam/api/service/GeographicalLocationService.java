package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
public interface GeographicalLocationService {

	Response createGeographicalLocations(List<GeographicalLocationDto> geographicalLocations);
}
