package fr.insee.pearljam.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "11. Enums", description = "Endpoints for enums")
@RequestMapping(path = "/api")
public class EnumController {

    @Operation(summary = "Get enum")
    @GetMapping(path = "/enum/state")
    public ResponseEntity<StateType[]> getStateEnum() {

        StateType[] enumValues = StateType.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

    @Operation(summary = "Get enum")
    @GetMapping(path = "/enum/contact-attempt")
    public ResponseEntity<Status[]> getContactAttemptEnum() {

        Status[] enumValues = Status.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

    @Operation(summary = "Get enum")
    @GetMapping(path = "/enum/contact-outcome")
    public ResponseEntity<ContactOutcomeType[]> getContactOutcomeEnum() {

        ContactOutcomeType[] enumValues = ContactOutcomeType.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

}
