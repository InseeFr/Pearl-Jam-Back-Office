package fr.insee.pearljam.api.domain;

import org.springframework.http.HttpStatus;

public class Response {
	/**
	 * label
	 */
	private String message;
	
	private HttpStatus httpStatus;

	/**
	 * Defaut constructor for a Status
	 * 
	 * @param label
	 */
	public Response(String message, HttpStatus httpStatus) {
		this.message = message;
		this.httpStatus = httpStatus;
	}

	/**
	 * Get the label for a Status
	 * 
	 * @return label
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the httpStatus
	 */
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	/**
	 * @param httpStatus the httpStatus to set
	 */
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
