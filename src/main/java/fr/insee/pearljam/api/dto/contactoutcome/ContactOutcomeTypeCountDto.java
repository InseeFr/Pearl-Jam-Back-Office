package fr.insee.pearljam.api.dto.contactoutcome;

import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
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
		initializeFields();
		dispatchAttributeValues(obj);

	}

	public ContactOutcomeTypeCountDto(Map<String, BigInteger> obj, CampaignDto campaign) {
		this.campaign = campaign;
		dispatchAttributeValues(obj);

	}

	public ContactOutcomeTypeCountDto(String idDem, String labelDem, Map<String, BigInteger> obj) {
		this(obj);
		this.idDem = idDem;
		this.setLabelDem(labelDem);
		dispatchAttributeValues(obj);
	}

	private void initializeFields() {
		for (String fieldName : Constants.CONTACT_OUTCOME_FIELDS) {
			setFieldValue(fieldName, 0L);
		}
	}

	private void dispatchAttributeValues(Map<String, BigInteger> obj) {
		if (obj != null) {
			for (Map.Entry<String, BigInteger> entry : obj.entrySet()) {
				String fieldName = entry.getKey();
				Long value = entry.getValue().longValue();
				setFieldValue(fieldName, value);
			}
		}
	}

	private void setFieldValue(String fieldName, Long value) {
		switch (fieldName) {
			case Constants.INA_COUNT:
				inaCount = value;
				break;
			case Constants.REF_COUNT:
				refCount = value;
				break;
			case Constants.IMP_COUNT:
				impCount = value;
				break;
			case Constants.UCD_COUNT:
				ucdCount = value;
				break;
			case Constants.UTR_COUNT:
				utrCount = value;
				break;
			case Constants.ALA_COUNT:
				alaCount = value;
				break;
			case Constants.DCD_COUNT:
				dcdCount = value;
				break;
			case Constants.NUH_COUNT:
				nuhCount = value;
				break;
			case Constants.DUK_COUNT:
				dukCount = value;
				break;
			case Constants.DUU_COUNT:
				duuCount = value;
				break;
			case Constants.NOA_COUNT:
				noaCount = value;
				break;
			case Constants.TOTAL_COUNT:
				total = value;
				break;
			// Add more cases for other fields if needed
			default:
				// Handle unknown field name
				break;
		}
	}

}
