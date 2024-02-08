package fr.insee.pearljam.api.dto.state;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import static fr.insee.pearljam.api.constants.Constants.*;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerContextDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Slf4j
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
		dispatchAttributeValues(obj, STATE_COUNT_FIELDS);
	}

	public StateCountDto(String idDem, String labelDem, Map<String, BigInteger> obj) {
		this(obj);
		this.idDem = idDem;
		this.setLabelDem(labelDem);
		dispatchAttributeValues(obj, STATE_COUNT_FIELDS);
	}

	public StateCountDto() {
		super();
	}

	public void addClosingCauseCount(Map<String, BigInteger> obj) {
		dispatchAttributeValues(obj, STATECOUNT_CLOSED_CLOSING_CAUSE_FIELDS);
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

	@Override
	public String toString() {
		return "StateCountDto [anvCount=" + anvCount + ", prcCount=" + prcCount + "]";
	}

}
