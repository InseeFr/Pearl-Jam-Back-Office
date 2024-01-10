package fr.insee.pearljam.api.dto.state;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import static fr.insee.pearljam.api.constants.Constants.*;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class StateCountDto {

	private String idDem;
	private String labelDem;
	private InterviewerContextDto interviewer;
	private CampaignDto campaign;
	private Long nvmCount;
	private Long nnsCount;
	private Long anvCount;
	private Long vinCount;
	private Long vicCount;
	private Long prcCount;
	private Long aocCount;
	private Long apsCount;
	private Long insCount;
	private Long wftCount;
	private Long wfsCount;
	private Long tbrCount;
	private Long finCount;
	private Long cloCount;
	private Long nvaCount;
	private Long npaCount;
	private Long npiCount;
	private Long npxCount;
	private Long rowCount;
	private Long total;

	public StateCountDto(Map<String, BigInteger> obj) {
		super();
		initializeFields(STATE_COUNT_FIELDS);
		dispatchAttributeValues(obj);
	}

	public StateCountDto(String idDem, String labelDem, Map<String, BigInteger> obj) {
		this(obj);
		this.idDem = idDem;
		this.setLabelDem(labelDem);
	}

	public StateCountDto() {
		super();
	}

	public void addClosingCauseCount(Map<String, BigInteger> obj) {
		initializeFields(STATECOUNT_CLOSED_CLOSING_CAUSE_FIELDS);
		dispatchAttributeValues(obj);
	}

	private void initializeFields(List<String> fields) {
		for (String fieldName : fields) {
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
			case NVM_COUNT:
				nvmCount = value;
				break;
			case NNS_COUNT:
				nnsCount = value;
				break;
			case ANV_COUNT:
				anvCount = value;
				break;
			case VIN_COUNT:
				vinCount = value;
				break;
			case VIC_COUNT:
				vicCount = value;
				break;
			case PRC_COUNT:
				prcCount = value;
				break;
			case AOC_COUNT:
				aocCount = value;
				break;
			case APS_COUNT:
				apsCount = value;
				break;
			case INS_COUNT:
				insCount = value;
				break;
			case WFT_COUNT:
				wftCount = value;
				break;
			case WFS_COUNT:
				wfsCount = value;
				break;
			case TBR_COUNT:
				tbrCount = value;
				break;
			case FIN_COUNT:
				finCount = value;
				break;
			case CLO_COUNT:
				cloCount = value;
				break;
			case NVA_COUNT:
				nvaCount = value;
				break;
			case TOTAL_COUNT:
				total = value;
				break;
			// Add more cases for other fields if needed
			default:
				// Handle unknown field name
				break;
		}
	}

	@Override
	public String toString() {
		return "StateCountDto [anvCount=" + anvCount + ", prcCount=" + prcCount + "]";
	}

}
