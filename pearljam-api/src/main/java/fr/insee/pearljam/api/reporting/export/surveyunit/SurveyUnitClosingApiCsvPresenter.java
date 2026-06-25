package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.export.progress.InterviewerProgressCsv;
import fr.insee.pearljam.api.reporting.export.progress.ProgressCsvRow;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.*;
import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.addRowWithTitleLabel;

@Component
public class SurveyUnitClosingApiCsvPresenter implements SurveyUnitClosingPresenter<SurveyUnitClosingCsv> {

    @Override
    public SurveyUnitClosingCsv present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates
    ) {

        return projections.stream()
                .map(toResponse(candidatesById, questionnaireStates))
                .toList();
    }

    private @NonNull Function<ClosableSurveyUnitView, SurveyUnitToCloseResponse> toResponse(Map<String, ClosableSurveyUnitCandidateView> candidatesById, Map<String, String> questionnaireStates) {
        return projection -> {
            String id = projection.getId();

            var candidate = candidatesById.get(id);

            ContactOutcomeType contactOutcome =
                    candidate != null ? candidate.getContactOutcomeType() : null;

            String interviewerLabel = buildInterviewerLabel(projection);

            String questionnaireState = questionnaireStates.getOrDefault(
                    id,
                    Constants.QUESTIONNAIRE_STATE_UNAVAILABLE
            );

            return new SurveyUnitToCloseResponse(
                    projection.getCampaignLabel(),
                    projection.getId(),
                    projection.getDisplayName(),
                    interviewerLabel,
                    projection.getSsech(),
                    computeIdentificationState(projection).name(),
                    contactOutcome,
                    questionnaireState,
                    projection.getClosingCauseType()
            );
        };
    }

    private String buildInterviewerLabel(ClosableSurveyUnitView projection) {
        String firstName = projection.getInterviewerFirstName();
        String lastName = projection.getInterviewerLastName();

        if (firstName == null && lastName == null) return null;

        return Stream.of(firstName, lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    private static IdentificationState computeIdentificationState(ClosableSurveyUnitView p) {
        return IdentificationState.getState(
                toModelIdentification(p),
                p.getCampaignIdentificationConfiguration()
        );
    }
    private static Identification toModelIdentification(ClosableSurveyUnitView p) {
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

    @Override
    public SurveyUnitClosingCsv empty() {
        return null;
    }
}


//@Component
//public class InterviewerProgressCsvPresenter
//        implements CampaignStatsByInterviewersPresenter<InterviewerProgressCsv> {
//
//    @Override
//    public InterviewerProgressCsv present(List<InterviewerDailyStats> interviewerStats,
//                                          CampaignDailyStats siteStats,
//                                          CampaignDailyStats campaignStats) {
//        List<CsvRow> rows = new ArrayList<>();
//        interviewerStats.forEach(interv ->
//                addRowWithMultipleTitleLabel(
//                        rows,
//                        List.of(interv.getInterviewerFirstName() + " " + interv.getInterviewerLastName(), interv.getInterviewerId()),
//                        ProgressCsvRow.commonValues(interv)));
//
//        addRowWithTitleLabel(rows, ProgressCsvRow.TOTAL_UNAFFECTED,
//                // 1 Column for Total France
//                // followed by 1 Column for Idep + Common values columns with emptyRowWithValueAtSpecificPosition
//                emptyRowWithValueAtSpecificPosition(campaignStats.getUnaffectedCount(), 2, ProgressCsvRow.commonValuesSize() + 1));
//        addRowWithTitleLabel(rows, ProgressCsvRow.TOTAL_SITE, ProgressCsvRow.commonValuesWithEmptyIdep((siteStats)));
//        addRowWithTitleLabel(rows, ProgressCsvRow.TOTAL_FRANCE, ProgressCsvRow.commonValuesWithEmptyIdep((campaignStats)));
//
//        return new InterviewerProgressCsv(rows);
//    }
//}

