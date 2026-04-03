package fr.insee.pearljam.domain.reporting.readmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class InterviewerDailyStats extends AbstractDailyStats {
    private String interviewerId;
    private String interviewerFirstName;
    private String interviewerLastName;
}
