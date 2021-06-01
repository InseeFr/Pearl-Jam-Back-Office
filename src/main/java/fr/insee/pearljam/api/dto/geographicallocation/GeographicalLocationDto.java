package fr.insee.pearljam.api.dto.geographicallocation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.insee.pearljam.api.domain.GeographicalLocation;

public class GeographicalLocationDto {
	private String id;
	private String label;
	
	
	public GeographicalLocationDto(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public GeographicalLocationDto(GeographicalLocation geographicalLocation) {
		super();
		if(geographicalLocation!=null) {
			this.id = geographicalLocation.getId();
			this.label = geographicalLocation.getLabel();
		}
	}
	
	
	public GeographicalLocationDto() {
		super();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "GeographicalLocationDto [id=" + id + ", label=" + label + "]";
	}
	
	@JsonIgnore
	public boolean isValid() {
		return this.label!=null && !this.label.isBlank() && this.id!=null && !this.id.isBlank();  
	}
	
}
