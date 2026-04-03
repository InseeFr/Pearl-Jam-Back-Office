package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;

final class ReportingPresenterTestData {

    private ReportingPresenterTestData() {
    }

    static CampaignDailyStats campaignStats(String campaignId, String campaignLabel, long unaffectedCount) {
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId(campaignId);
        stats.setCampaignLabel(campaignLabel);
        stats.setUnaffectedCount(unaffectedCount);
        applyBaseStats(stats);
        return stats;
    }

    static InterviewerDailyStats interviewerStats(String firstName, String lastName) {
        InterviewerDailyStats stats = new InterviewerDailyStats();
        stats.setInterviewerFirstName(firstName);
        stats.setInterviewerLastName(lastName);
        applyBaseStats(stats);
        return stats;
    }

    static OrganizationUnitDailyStats organizationUnitStats(String label) {
        OrganizationUnitDailyStats stats = new OrganizationUnitDailyStats();
        stats.setOuLabel(label);
        applyBaseStats(stats);
        return stats;
    }

    private static void applyBaseStats(AbstractDailyStats stats) {
        stats.setAnvStateCount(1L);
        stats.setVinStateCount(2L);
        stats.setVicStateCount(3L);
        stats.setPrcStateCount(4L);
        stats.setAocStateCount(5L);
        stats.setApsStateCount(6L);
        stats.setInsStateCount(7L);
        stats.setWftStateCount(8L);
        stats.setWfsStateCount(9L);
        stats.setTbrStateCount(10L);
        stats.setFinStateCount(11L);
        stats.setCloStateCount(12L);

        stats.setNoticeCommunicationCount(13L);
        stats.setReminderCommunicationCount(14L);

        stats.setInaContactOutcomeCount(15L);
        stats.setRefContactOutcomeCount(16L);
        stats.setImpContactOutcomeCount(17L);
        stats.setUcdContactOutcomeCount(18L);
        stats.setUtrContactOutcomeCount(19L);
        stats.setAlaContactOutcomeCount(20L);
        stats.setDukContactOutcomeCount(21L);
        stats.setNuhContactOutcomeCount(22L);
        stats.setNoaContactOutcomeCount(23L);

        stats.setNpaClosingCauseCount(24L);
        stats.setNpiClosingCauseCount(25L);
        stats.setNpxClosingCauseCount(26L);
        stats.setRowClosingCauseCount(27L);
    }
}
