package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.springframework.data.domain.Page;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Presenter interface for formatting survey units assigned responses.
 * Implementations of this interface are responsible for transforming the domain model
 * into the appropriate response format.
 *
 * @param <T> the type of response this presenter produces
 */
public interface SurveyUnitAssignedPresenter<T> {

    /**
     * Presents the paginated survey units to review in the appropriate response format.
     *
     * @param surveyUnits the paginated survey units to present
     * @return the formatted response
     */
    T present(Page<SurveyUnitAssigned> surveyUnits);

    static String buildInterviewerLabel(SurveyUnitAssigned surveyUnitAssigned) {
        String firstName = surveyUnitAssigned.interviewerFirstName();
        String lastName = surveyUnitAssigned.interviewerLastName();
        return buildInterviewerLabel(firstName, lastName);
    }

    static String buildInterviewerLabel(String firstName, String lastName) {
        String result = Stream.of(firstName, lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));

        return result.isBlank() ? "" : result;
    }

}
