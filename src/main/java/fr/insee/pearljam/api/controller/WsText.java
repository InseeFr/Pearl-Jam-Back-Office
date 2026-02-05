package fr.insee.pearljam.api.controller;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WsText implements Serializable{

	@Serial
	private static final long serialVersionUID = 1L;

    @JsonProperty("text")
	private String text;

}