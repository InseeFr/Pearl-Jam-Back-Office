package fr.insee.pearljam.api.dto.state;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;

public class StateDto {
	
	/**
	 * The id of the StateDto
	 */
	private Long id;
	
	/**
	 * The date of the StateDto
	 */
	private Long date;
    
    /**
     * The type of the StateDto
     */
	private StateType type;

    
	public StateDto(Long id, Long date, StateType type) {
		super();
		this.id = id;
		this.date = date;
		this.type = type;
	}
	
	public StateDto(State state) {
		super();
		this.id = state.getId();
		this.date = state.getDate();
		this.type = state.getType();
	}
	
	public StateDto() {
		super();
	}

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

	@Override
	public String toString() {
		return "StateDto [id=" + id + ", date=" + date + ", type=" + type + "]";
	}
	
}
