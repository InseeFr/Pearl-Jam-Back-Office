package fr.insee.pearljam.api.web.exception;

import fr.insee.pearljam.domain.campaign.service.exception.*;
import fr.insee.pearljam.domain.surveyunit.service.exception.InterviewerNotFoundException;
import fr.insee.pearljam.domain.message.service.exception.SendMailException;
import fr.insee.pearljam.domain.organizationunit.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserAlreadyExistsException;
import fr.insee.pearljam.domain.shared.exception.EntityAlreadyExistException;
import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserNotAssociatedToCampaignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;

import java.net.URI;

/**
 * Handle API exceptions for project
 * Do not work on exceptions occuring before/outside controllers scope
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    public static final String ERROR_OCCURRED_LABEL = "An error has occurred";
    public static final String INVALID_PARAMETERS_MESSAGE = "Invalid request content.";

    /**
     * Global method to process the catched exception
     *
     * @param ex      Exception catched
     * @param status  status linked with this exception
     * @return the apierror object with linked status code
     */
    private ProblemDetail generateResponseError(Exception ex, HttpStatus status) {
        return generateResponseError(ex, status, null, null);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param problemType          problem type
     * @param overrideErrorMessage message overriding default error message from exception
     * @return the apierror object with linked status code
     */
    private ProblemDetail generateResponseError(Exception ex, HttpStatus status, URI problemType, String overrideErrorMessage) {
        log.error(ex.getMessage(), ex);
        String errorMessage = ex.getMessage();
        if (overrideErrorMessage != null) {
            errorMessage = overrideErrorMessage;
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, errorMessage);
        if(problemType != null) {
            problemDetail.setType(problemType);
        }
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accessDeniedException(AccessDeniedException e) {
        return generateResponseError(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);

        Throwable rootCause = e.getRootCause();

        String errorMessage = "Error when deserializing JSON";
        if (rootCause instanceof StreamReadException parseException) {
            String location = parseException.getLocation() != null ? "[line: " + parseException.getLocation().getLineNr() + ", column: " + parseException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error with JSON syntax. Check that your json is well formatted: " + location;
        }
        if (rootCause instanceof DatabindException mappingException) {
            String location = mappingException.getLocation() != null ? "[line: " + mappingException.getLocation().getLineNr() + ", column: " + mappingException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error when deserializing JSON. Check that your JSON properties are of the expected types " + location;
        }
        return generateResponseError(e, HttpStatus.BAD_REQUEST, null, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail noEntityFoundException(EntityNotFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CampaignNotFoundException.class)
    public ProblemDetail noCampaignFoundException(CampaignNotFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ProblemDetail entityAlreadyExistException(EntityAlreadyExistException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoOrganizationUnitException.class)
    public ProblemDetail exceptions(NoOrganizationUnitException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail exceptions(BadRequestException e) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommunicationTemplateNotFoundException.class)
    public ProblemDetail exceptions(CommunicationTemplateNotFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SurveyUnitNotFoundException.class)
    public ProblemDetail exceptions(SurveyUnitNotFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail exceptions(ConflictException e) {
        return generateResponseError(e, HttpStatus.CONFLICT, null, e.getGlobalMessage());
    }

    @ExceptionHandler(VisibilityNotFoundException.class)
    public ProblemDetail exceptions(VisibilityNotFoundException e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(VisibilityHasInvalidDatesException.class)
    public ProblemDetail exceptions(VisibilityHasInvalidDatesException e) {
        return generateResponseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CampaignOnGoingException.class)
    public ProblemDetail exceptions(CampaignOnGoingException e) {
        return generateResponseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ProblemDetail exceptions(HttpClientErrorException e) {
        return generateResponseError(e, HttpStatus.valueOf(e.getStatusCode().value()), null, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(SendMailException.class)
    public ProblemDetail exceptions(SendMailException e) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, null, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(UserNotAssociatedToCampaignException.class)
    public ProblemDetail userCampaignMismatch(Exception e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InterviewerNotFoundException.class)
    public ProblemDetail interviewerNotFoundException(Exception e) {
        return generateResponseError(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail existingUserOnCreationException(Exception e) {
        return generateResponseError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail exceptions(Exception e) {
        if(e instanceof ErrorResponse err) {
            return err.getBody();
        }
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, URI.create("about:blank"), ERROR_OCCURRED_LABEL);
    }
}