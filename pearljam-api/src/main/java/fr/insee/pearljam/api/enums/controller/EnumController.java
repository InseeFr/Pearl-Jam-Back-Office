package fr.insee.pearljam.api.enums.controller;

import fr.insee.pearljam.api.campaign.controller.EndpointDisabledException;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "11. Enums", description = "Endpoints for enums")
@RequiredArgsConstructor
public class EnumController {

    @Value("${feature.deprecated.endpoints.enabled}")
    private final boolean deprecatedEndpointsEnabled;

    /**
     * @deprecated
     * @return state enum
     */
    @Operation(summary = "Get enum")
    @GetMapping(Constants.API_ENUM_STATE)
    @Deprecated(forRemoval = true)
    public ResponseEntity<StateType[]> getStateEnum() {
        if(!deprecatedEndpointsEnabled) {
            throw new EndpointDisabledException();
        }
        StateType[] enumValues = StateType.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

    /**
     * @deprecated
     * @return contact enum
     */
    @Operation(summary = "Get enum")
    @GetMapping(Constants.API_ENUM_CONTACT_ATTEMPT)
    @Deprecated(forRemoval = true)
    public ResponseEntity<Status[]> getContactAttemptEnum() {
        if(!deprecatedEndpointsEnabled) {
            throw new EndpointDisabledException();
        }
        Status[] enumValues = Status.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

    /**
     * @deprecated
     * @return contact outcome enums
     */
    @Operation(summary = "Get enum")
    @GetMapping(Constants.API_ENUM_CONTACT_OUTCOME)
    @Deprecated(forRemoval = true)
    public ResponseEntity<ContactOutcomeType[]> getContactOutcomeEnum() {
        if(!deprecatedEndpointsEnabled) {
            throw new EndpointDisabledException();
        }
        ContactOutcomeType[] enumValues = ContactOutcomeType.values();
        return new ResponseEntity<>(enumValues, HttpStatus.OK);
    }

}
