package fr.insee.pearljam.infrastructure.mail.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class Recipient {

    @JsonProperty(value = "Address", required = true)
    protected String address;
}
