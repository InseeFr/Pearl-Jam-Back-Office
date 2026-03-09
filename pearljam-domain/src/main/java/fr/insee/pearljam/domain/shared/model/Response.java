package fr.insee.pearljam.domain.shared.model;

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
