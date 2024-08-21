package fr.insee.pearljam.domain.campaign.model;

import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisibilityTest {

    @Test
    @DisplayName("Should merge 2 visibilities and validate the merged one")
    void testMergeAndDateValidation() throws VisibilityHasInvalidDatesException {
        // Given
        Visibility currentVisibility = new Visibility("campaign1", "OU1",
                1627845600000L, 1627932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);

        Visibility updateVisibility = new Visibility("campaign1", "OU1",
                1617933000000L, 1627933000000L, 1627934000000L, 1627935000000L, 1627936000000L, 1628277601000L);

        // When
        Visibility mergedVisibility = Visibility.merge(currentVisibility, updateVisibility);

        // Then
        assertThat(mergedVisibility.campaignId()).isEqualTo("campaign1");
        assertThat(mergedVisibility.organizationalUnitId()).isEqualTo("OU1");
        assertThat(mergedVisibility.managementStartDate()).isEqualTo(1617933000000L);
        assertThat(mergedVisibility.interviewerStartDate()).isEqualTo(1627933000000L);
        assertThat(mergedVisibility.identificationPhaseStartDate()).isEqualTo(1627934000000L);
        assertThat(mergedVisibility.collectionStartDate()).isEqualTo(1627935000000L);
        assertThat(mergedVisibility.collectionEndDate()).isEqualTo(1627936000000L);
        assertThat(mergedVisibility.endDate()).isEqualTo(1628277601000L);
    }

    @Test
    @DisplayName("Should merge 2 visibilities and keep original dates when updated dates are null")
    void testMergeNullDates() throws VisibilityHasInvalidDatesException {
        // Given
        Visibility currentVisibility = new Visibility("campaign1", "OU1",
                1627845600000L, 1627932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);

        Visibility updateVisibility = new Visibility("campaign1", "OU1",
                null, null, null, null, null, null);

        // When
        Visibility mergedVisibility = Visibility.merge(currentVisibility, updateVisibility);

        // Then
        assertThat(mergedVisibility.campaignId()).isEqualTo("campaign1");
        assertThat(mergedVisibility.organizationalUnitId()).isEqualTo("OU1");
        assertThat(mergedVisibility.managementStartDate()).isEqualTo(1627845600000L);
        assertThat(mergedVisibility.interviewerStartDate()).isEqualTo(1627932000000L);
        assertThat(mergedVisibility.identificationPhaseStartDate()).isEqualTo(1628018400000L);
        assertThat(mergedVisibility.collectionStartDate()).isEqualTo(1628104800000L);
        assertThat(mergedVisibility.collectionEndDate()).isEqualTo(1628191200000L);
        assertThat(mergedVisibility.endDate()).isEqualTo(1628277600000L);
    }

    @Test
    @DisplayName("Should throw exception when merged visibilities dates are invalid")
    void testMergeThrowsExceptionForInvalidDates() {
        // Given
        Visibility currentVisibility = new Visibility("campaign1", "OU1",
                1327845600000L, 1327932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);

        Visibility updateVisibility = new Visibility("campaign1", "OU1",
                1227845600000L, 1327932000001L, 1327932000002L, 1327932000003L, 1327932000004L, 1327932000004L);

        // When & Then
        assertThatThrownBy(() -> Visibility.merge(currentVisibility, updateVisibility))
                .isInstanceOf(VisibilityHasInvalidDatesException.class)
                .hasMessage(VisibilityHasInvalidDatesException.MESSAGE);
    }

    @ParameterizedTest
    @CsvSource({
            "1327932000000,1327932000001,1327932000002,1327932000003,1327932000004,1327932000004",
            "1327932000000,1327932000001,1327932000002,1327932000003,1327932000003,1327932000004",
            "1327932000000,1327932000001,1327932000002,1327932000002,1327932000003,1327932000004",
            "1327932000000,1327932000001,1327932000001,1327932000002,1327932000003,1327932000004",
            "1327932000000,1327932000000,1327932000001,1327932000002,1327932000003,1327932000004"
    })
    @DisplayName("Should throw exception when merged visibilities dates are invalid")
    void testMergeThrowsExceptionForInvalidDates2(String managementStartDate, String interviwerStartDate, String identificationPhaseStartDate, String collectionStartDate, String collectionEndDate, String endDate) {
        // Given
        Visibility currentVisibility = new Visibility("campaign1", "OU1",
                1327845600000L, 1327932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);

        Visibility updateVisibility = new Visibility("campaign1", "OU1",
                getLong(managementStartDate), getLong(interviwerStartDate),
                getLong(identificationPhaseStartDate), getLong(collectionStartDate),
                getLong(collectionEndDate), getLong(endDate));

        // When & Then
        assertThatThrownBy(() -> Visibility.merge(currentVisibility, updateVisibility))
                .isInstanceOf(VisibilityHasInvalidDatesException.class)
                .hasMessage(VisibilityHasInvalidDatesException.MESSAGE);
    }

    private Long getLong(String date) {
        return Long.parseLong(date);
    }
}
