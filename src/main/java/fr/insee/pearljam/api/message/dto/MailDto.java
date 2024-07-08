package fr.insee.pearljam.api.message.dto;

import jakarta.validation.constraints.NotNull;


public record MailDto(
	@NotNull
	String content,
	@NotNull
	String subject) {
}
