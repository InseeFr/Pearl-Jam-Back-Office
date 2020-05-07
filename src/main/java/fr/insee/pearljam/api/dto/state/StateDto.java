package fr.insee.pearljam.api.dto.state;

import fr.insee.pearljam.api.domain.StateType;

public class StateDto {
	
	/**
	 * The id of the StateDto
	 */
	Long id;
	
	/**
	 * The date of the StateDto
	 */
    Long date;
    
    /**
     * The type of the StateDto
     */
    StateType type;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}

	/**
	 * @return the type
	 */
	public StateType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(StateType type) {
		this.type = type;
	}
}
