package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesInterviewerProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ProgressCsvRow {

    static List<Object> commonValues(float progressRate,
                                     StatesProgressResponse states,
                                     CommunicationsProgressResponse communications) {
        return List.of(
                progressRate,
                states.allocated(), states.notStarted(), states.inProgress(),
                states.pendingTransmission(), states.toReview(), states.validated(),
                states.preparingContact(), states.withContact(), states.withAppointment(),
                states.started(),
                communications.noticeLetter(), communications.reminderLetter()
        );
    }

    static List<Object> commonValues(float progressRate,
                                     StatesInterviewerProgressResponse states,
                                     CommunicationsProgressResponse communications) {
        return List.of(
                progressRate,
                states.allocatedInterviewers(), states.notStarted(), states.inProgress(),
                states.pendingTransmission(), states.toReview(), states.validated(),
                states.preparingContact(), states.withContact(), states.withAppointment(),
                states.started(),
                communications.noticeLetter(), communications.reminderLetter()
        );
    }
}
