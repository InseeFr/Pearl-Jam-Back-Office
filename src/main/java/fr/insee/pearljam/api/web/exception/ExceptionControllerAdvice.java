package fr.insee.pearljam.api.web.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.domain.exception.*;
import fr.insee.pearljam.infrastructure.mail.exception.SendMailException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Handle API exceptions for project
 * Do not work on exceptions occuring before/outside controllers scope
 */
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    private final ApiExceptionComponent errorComponent;

    public ExceptionControllerAdvice(ErrorAttributes errorAttributes) {
        this.errorComponent = new ApiExceptionComponent(errorAttributes);
    }

    public static final String ERROR_OCCURRED_LABEL = "An error has occurred";
    public static final String INVALID_PARAMETERS_MESSAGE = "Invalid parameters";

    /**
     * Global method to process the catched exception
     *
     * @param ex      Exception catched
     * @param status  status linked with this exception
     * @param request request initiating the exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> generateResponseError(Exception ex, HttpStatus status, WebRequest request) {
        return generateResponseError(ex, status, request, null);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param request              request initiating the exception
     * @param overrideErrorMessage message overriding default error message from exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> generateResponseError(Exception ex, HttpStatus status, WebRequest request, String overrideErrorMessage) {
        log.error(ex.getMessage(), ex);
        String errorMessage = ex.getMessage();
        if (overrideErrorMessage != null) {
            errorMessage = overrideErrorMessage;
        }
        ApiError error = errorComponent.buildApiErrorObject(request, status, errorMessage);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> noHandlerFoundException(NoHandlerFoundException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accessDeniedException(AccessDeniedException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request, INVALID_PARAMETERS_MESSAGE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException e,
            WebRequest request) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request, "Invalid data");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, WebRequest request) {
        log.error(e.getMessage(), e);

        Throwable rootCause = e.getRootCause();

        String errorMessage = "Error when deserializing JSON";
        if (rootCause instanceof JsonParseException parseException) {
            String location = parseException.getLocation() != null ? "[line: " + parseException.getLocation().getLineNr() + ", column: " + parseException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error with JSON syntax. Check that your json is well formatted: " + location;
        }
        if (rootCause instanceof JsonMappingException mappingException) {
            String location = mappingException.getLocation() != null ? "[line: " + mappingException.getLocation().getLineNr() + ", column: " + mappingException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error when deserializing JSON. Check that your JSON properties are of the expected types " + location;
        }
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> noEntityFoundException(EntityNotFoundException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<ApiError> entityAlreadyExistException(EntityAlreadyExistException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NoOrganizationUnitException.class)
    public ResponseEntity<ApiError> exceptions(NoOrganizationUnitException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(CommunicationTemplateNotFoundException.class)
    public ResponseEntity<ApiError> exceptions(CommunicationTemplateNotFoundException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(SurveyUnitNotFoundException.class)
    public ResponseEntity<ApiError> exceptions(SurveyUnitNotFoundException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(VisibilityNotFoundException.class)
    public ResponseEntity<ApiError> exceptions(VisibilityNotFoundException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(VisibilityHasInvalidDatesException.class)
    public ResponseEntity<ApiError> exceptions(VisibilityHasInvalidDatesException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(CampaignOnGoingException.class)
    public ResponseEntity<ApiError> exceptions(CampaignOnGoingException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiError> exceptions(HttpClientErrorException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.valueOf(e.getStatusCode().value()), request, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(SendMailException.class)
    public ResponseEntity<ApiError> exceptions(SendMailException e, WebRequest request) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, request, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> exceptions(Exception e, WebRequest request) {
        return generateResponseError(e, HttpStatus.INTERNAL_SERVER_ERROR, request, ERROR_OCCURRED_LABEL);
    }
}