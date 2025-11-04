package fr.insee.pearljam.message.infrastructure.rest.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WsTextDto implements Serializable{
  
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	@JsonProperty("text")
	private String text;

	public WsTextDto(String text){
		super();
    	this.text = text;
	}
  
	public WsTextDto(){
	    super();

	}
  
	/**
	 * @return the last name of the MessageRecipient
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param the last name of the MessageRecipient
	 */
	public void setText(String text) {
		this.text = text;
	}
}