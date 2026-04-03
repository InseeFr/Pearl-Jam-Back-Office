package fr.insee.pearljam.domain.reporting.readmodel.stats;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrganizationUnitDailyStats extends AbstractDailyStats {
    private String ouId;
    private String ouLabel;
}
