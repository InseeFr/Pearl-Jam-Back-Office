package fr.insee.pearljam.api.surveyunit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.repository.ClosableSurveyUnitCandidateProjection;
import fr.insee.pearljam.api.service.impl.ClosableSurveyUnitProjection;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClosableSurveyUnitDto {

    private String id;
    private String displayName;
    private Integer ssech;
    private String location;
    private String city;
    private Long finalizationDate;
    private String campaign;
    private ClosingCauseType closingCause;
    private StateType state;
    private String questionnaireState;
    private ClosableContactOutcomeDto contactOutcome;
    private IdentificationState identificationState;
    private ClosableInterviewerDto interviewer;

    public static ClosableSurveyUnitDto from(ClosableSurveyUnitCandidateProjection candidate, ClosableSurveyUnitProjection projection, String questionnaireState) {
        return new ClosableSurveyUnitDto(
                candidate.getId(),
                projection.getDisplayName(),
                projection.getSsech(),
                computeLocation(projection.getAddressL6()),
                computeCity(projection.getAddressL6()),
                projection.getFinalizationDate(),
                projection.getCampaignLabel(),
                computeClosingCause(candidate, projection),
                candidate.getCurrentStateType(),
                questionnaireState,
                candidate.getContactOutcomeType() == null ? null : new ClosableContactOutcomeDto(candidate.getContactOutcomeType()),
                computeIdentificationState(projection),
                new ClosableInterviewerDto(projection.getInterviewerFirstName(), projection.getInterviewerLastName())
        );
    }

    private static String computeLocation(String l6) {
        if (l6 == null) return null;
        String trimmed = l6.trim();
        int idx = trimmed.indexOf(' ');
        return idx > 0 ? trimmed.substring(0, idx) : null;
    }

    private static String computeCity(String l6) {
        if (l6 == null) return null;
        String trimmed = l6.trim();
        int idx = trimmed.indexOf(' ');
        return idx > 0 ? trimmed.substring(idx + 1) : null;
    }

    private static ClosingCauseType computeClosingCause(ClosableSurveyUnitCandidateProjection candidate, ClosableSurveyUnitProjection projection) {
        if (projection.getClosingCauseType() != null
                && candidate.getCurrentStateType() != StateType.CLO) {
            return projection.getClosingCauseType();
        }
        return null;
    }

    private static Identification toModelIdentification(ClosableSurveyUnitProjection p) {
        boolean allNull =
                p.getIdentification() == null
                        && p.getAccess() == null
                        && p.getSituation() == null
                        && p.getCategory() == null
                        && p.getOccupant() == null
                        && p.getIndividualStatus() == null
                        && p.getInterviewerCanProcess() == null
                        && p.getNumberOfRespondents() == null
                        && p.getPresentInPreviousHome() == null
                        && p.getHouseholdComposition() == null
                        && p.getIdentificationType() == null;

        if (allNull) return null;

        return Identification.builder()
                .identificationType(p.getIdentificationType())
                .identification(p.getIdentification())
                .access(p.getAccess())
                .situation(p.getSituation())
                .category(p.getCategory())
                .occupant(p.getOccupant())
                .individualStatus(p.getIndividualStatus())
                .interviewerCanProcess(p.getInterviewerCanProcess())
                .numberOfRespondents(p.getNumberOfRespondents())
                .presentInPreviousHome(p.getPresentInPreviousHome())
                .householdComposition(p.getHouseholdComposition())
                .build();
    }

    private static IdentificationState computeIdentificationState(ClosableSurveyUnitProjection p) {
        return IdentificationState.getState(
                toModelIdentification(p),
                p.getCampaignIdentificationConfiguration()
        );
    }
}
