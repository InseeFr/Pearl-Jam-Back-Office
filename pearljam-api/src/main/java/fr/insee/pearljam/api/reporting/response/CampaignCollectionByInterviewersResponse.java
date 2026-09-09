package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignCollectionByInterviewers")
public record CampaignCollectionByInterviewersResponse(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign,
        long updatedAt) {

    @Schema(name = "CampaignCollectionByInterviewersInterviewer")
    public record Interviewer(
            String interviewerLabel,
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    @Schema(name = "CampaignCollectionByInterviewersOU")
    public record OrganizationUnit(
            long allocated,
            long unaffected,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    @Schema(name = "CampaignCollectionByInterviewersCampaign")
    public record Campaign(
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}
}
