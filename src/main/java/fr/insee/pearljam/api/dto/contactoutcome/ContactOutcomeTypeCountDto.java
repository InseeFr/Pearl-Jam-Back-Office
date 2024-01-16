package fr.insee.pearljam.api.dto.contactoutcome;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import static fr.insee.pearljam.api.constants.Constants.CONTACT_OUTCOME_FIELDS;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

	public ContactOutcomeTypeCountDto(Map<String, BigInteger> obj) {
		dispatchAttributeValues(obj, CONTACT_OUTCOME_FIELDS);
	}

	public ContactOutcomeTypeCountDto(Map<String, BigInteger> obj, CampaignDto campaign) {
		this.campaign = campaign;
		dispatchAttributeValues(obj, CONTACT_OUTCOME_FIELDS);
	}

	public ContactOutcomeTypeCountDto(String idDem, String labelDem, Map<String, BigInteger> obj) {
		this(obj);
		this.idDem = idDem;
		this.setLabelDem(labelDem);
		dispatchAttributeValues(obj, CONTACT_OUTCOME_FIELDS);
	}

	private void dispatchAttributeValues(Map<String, BigInteger> obj, List<String> fieldKeys) {
		boolean nullOrEmpty = Optional.ofNullable(obj.isEmpty()).orElse(true);
		for (String str : fieldKeys) {
			if (nullOrEmpty) {
				setLongField(str, 0L);
			} else {
				setLongField(str, Optional.ofNullable(obj.get(str)).orElse(BigInteger.ZERO).longValue());
			}
		}
	}

	private void setLongField(String fieldName, Long value) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.set(this, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			log.warn("Couldn't set field {} with value {}", fieldName, value);
		}
	}

}
