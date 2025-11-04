package fr.insee.pearljam.message.infrastructure.http.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class Recipients {
    @JsonProperty("Recipient")
    protected List<Recipient> recipient = new ArrayList<>();
}
