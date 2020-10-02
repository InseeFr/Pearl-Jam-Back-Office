package fr.insee.pearljam.api.controller;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WsText implements Serializable{
  
	@JsonProperty("text")
	private String text;

	public WsText(String text){
		super();
    	this.text = text;
	}
  
	public WsText(){
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