package fr.insee.pearljam.message.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;


public record MailDto(
	@NotNull
	String content,
	@NotNull
	String subject) {
}
