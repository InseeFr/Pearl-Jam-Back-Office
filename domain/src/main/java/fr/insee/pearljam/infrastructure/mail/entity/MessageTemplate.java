package fr.insee.pearljam.infrastructure.mail.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({ "sender", "subject", "content"})
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class MessageTemplate {

    @JsonProperty(value = "Sender", required = true)
    protected String sender;

    @JsonProperty(value = "Subject", required = true)
    protected String subject;

    @JsonProperty("Content")
    protected String content;
}
