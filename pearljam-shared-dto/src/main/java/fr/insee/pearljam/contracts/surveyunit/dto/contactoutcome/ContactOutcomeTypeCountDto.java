package fr.insee.pearljam.contracts.surveyunit.dto.contactoutcome;

import static fr.insee.pearljam.contracts.constants.Constants.CONTACT_OUTCOME_FIELDS;

import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactOutcomeTypeCountDto {

  private String idDem;

  private String labelDem;

  private CampaignDto campaign;

  private Long inaCount;

  private Long refCount;

  private Long impCount;

  private Long ucdCount;

  private Long utrCount;

  private Long alaCount;

  private Long dcdCount;

  private Long nuhCount;

  private Long dukCount;

  private Long duuCount;

  private Long noaCount;

  private Long total;
}
