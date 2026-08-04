package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.springframework.data.domain.Page;

public interface SurveyUnitCompletedPresenter<T> {
    T present(Page<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits);

    default T empty() {
        return null;
    }

    default String getInterviewerLabel(SurveyUnitFetchedByStatesAndCampaignIdView su) {
        String firstName = su.interviewerFirstName() != null ? su.interviewerFirstName() : "";
        String lastName = su.interviewerLastName() != null ? su.interviewerLastName() : "";
        if (firstName.isEmpty() && lastName.isEmpty()) {
            return "";
        }
        if (firstName.isEmpty()) {
            return lastName;
        }
        if (lastName.isEmpty()) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
