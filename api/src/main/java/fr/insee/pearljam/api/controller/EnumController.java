package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "11. Enums", description = "Endpoints for enums")
public class EnumController {

    @Operation(summary = "Get enum")
    @GetMapping(Constants.API_ENUM_STATE)
    public ResponseEntity<StateType[]> getStateEnum() {

        StateType[] enumValues = StateType.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

    @Operation(summary = "Get enum")
    @GetMapping(Constants.API_ENUM_CONTACT_ATTEMPT)
    public ResponseEntity<Status[]> getContactAttemptEnum() {

        Status[] enumValues = Status.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

    @Operation(summary = "Get enum")
    @GetMapping(Constants.API_ENUM_CONTACT_OUTCOME)
    public ResponseEntity<ContactOutcomeType[]> getContactOutcomeEnum() {

        ContactOutcomeType[] enumValues = ContactOutcomeType.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

}
