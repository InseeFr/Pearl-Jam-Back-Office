package fr.insee.pearljam.domain.campaign;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CampaignPreferenceModel {
     private final String id;
    private final String label;
    private final boolean preference;
}
