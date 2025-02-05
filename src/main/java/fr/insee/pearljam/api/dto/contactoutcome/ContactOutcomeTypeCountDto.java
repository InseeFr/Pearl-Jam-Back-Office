package fr.insee.pearljam.api.dto.contactoutcome;

import static fr.insee.pearljam.api.constants.Constants.CONTACT_OUTCOME_FIELDS;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
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

  public ContactOutcomeTypeCountDto() {
    super();
  }

  public ContactOutcomeTypeCountDto(Map<String, Long> obj) {
    dispatchAttributeValues(obj, CONTACT_OUTCOME_FIELDS);
  }

  public ContactOutcomeTypeCountDto(Map<String, Long> obj, CampaignDto campaign) {
    this.campaign = campaign;
    dispatchAttributeValues(obj, CONTACT_OUTCOME_FIELDS);
  }

  public ContactOutcomeTypeCountDto(String idDem, String labelDem, Map<String, Long> obj) {
    this(obj);
    this.idDem = idDem;
    this.labelDem = labelDem;
    dispatchAttributeValues(obj, CONTACT_OUTCOME_FIELDS);
  }

  @SuppressWarnings("null") // to refactor with typed input
  private void dispatchAttributeValues(Map<String, Long> obj, List<String> fieldKeys) {
    boolean nullOrEmpty = (obj == null || obj.isEmpty());
    for (String str : fieldKeys) {
      if (nullOrEmpty) {
        setLongField(str, 0L);
      } else {
        setLongField(str, Optional.ofNullable(obj.get(str)).orElse(0L));
      }
    }
  }

  private void setLongField(String fieldName, Long value) {
    try {
      Field field = getClass().getDeclaredField(fieldName);
      field.set(this, value);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException |
             IllegalAccessException e) {
      log.warn("Couldn't set field {} with value {}", fieldName, value);
    }
  }

}
