package fr.insee.pearljam.message.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({ "messageTemplate", "recipients" })
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class SendRequest {

    @JsonProperty(value = "MessageTemplate", required = true)
    protected MessageTemplate messageTemplate;

    @JsonProperty(value = "Recipients", required = true)
    protected Recipients recipients;
}

