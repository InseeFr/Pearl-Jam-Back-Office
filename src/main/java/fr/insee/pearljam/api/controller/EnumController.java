package fr.insee.pearljam.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.Status;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class EnumController {

    @ApiOperation(value = "Get enum")
    @GetMapping(path = "/enum/state")
    public ResponseEntity<StateType[]> getStateEnum() {

        StateType[] enumValues = StateType.values();
        return new ResponseEntity<StateType[]>(enumValues, HttpStatus.OK);
    }

    @ApiOperation(value = "Get enum")
    @GetMapping(path = "/enum/contact-attempt")
    public ResponseEntity<Status[]> getContactAttemptEnum() {

        Status[] enumValues = Status.values();
        return new ResponseEntity<Status[]>(enumValues, HttpStatus.OK);
    }

    @ApiOperation(value = "Get enum")
    @GetMapping(path = "/enum/contact-outcome")
    public ResponseEntity<ContactOutcomeType[]> getContactOutcomeEnum() {

        ContactOutcomeType[] enumValues = ContactOutcomeType.values();
        return new ResponseEntity<ContactOutcomeType[]>(enumValues, HttpStatus.OK);
    }

}
