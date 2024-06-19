package fr.insee.pearljam.api.domain;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Response {

	private String message;
	private HttpStatus httpStatus;

}
